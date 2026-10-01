package io.hexlet.cv.security;

import io.hexlet.cv.config.JwtProperties;
import io.hexlet.cv.model.RefreshToken;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.JWTUtils;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

/**
 * Ротация refresh-токенов с детектом повторного использования.
 *
 * @Transactional здесь нет намеренно: отзыв семейства при детекте повтора должен
 * закоммититься до броска BadCredentialsException. Транзакционные границы — в
 * RefreshTokenStore.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TokenService {

    private final AuthenticationManager authenticationManager;
    private final JWTUtils jwtUtils;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;
    private final RefreshTokenStore store;

    /** Логин и регистрация: новая сессия — новое семейство. */
    public Tokens authenticateAndGenerate(String email, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return startNewSession(requireUser(email));
    }

    public RefreshResult refresh(String refreshToken) {
        Jwt jwt = decodeOrReject(refreshToken);
        User user = requireUser(jwt.getSubject());

        UUID presentedJti = parseUuidOrNull(jwt.getId());
        UUID familyId = parseUuidOrNull(jwt.getClaimAsString(JWTUtils.CLAIM_FAMILY_ID));
        if (presentedJti == null || familyId == null) {
            return new RefreshResult(handleLegacyToken(user), user.getEmail());
        }

        Instant now = Instant.now();
        Issued issued = issue(user, familyId, now);

        // Атомарно: погасить предъявленный, записать преемника.
        // false означает «токен не был активен» — либо кража, либо гонка.
        if (store.rotate(presentedJti, issued.record(), now)) {
            return new RefreshResult(issued.tokens(), user.getEmail());
        }

        if (isBenignRace(presentedJti, now)) {
            log.info("Concurrent refresh ignored: jti={} familyId={} userId={}",
                    presentedJti, familyId, user.getId());
            throw new BadCredentialsException("Concurrent refresh");
        }

        int revoked = store.revokeFamily(familyId, now);
        log.warn("Refresh token reuse detected: jti={} familyId={} userId={} sessionsRevoked={}",
                presentedJti, familyId, user.getId(), revoked);
        throw new BadCredentialsException("Refresh token already used");
    }

    /** Logout: гасит только предъявленную сессию, остальные устройства остаются живыми. */
    public void revokeByRefreshToken(String refreshToken) {
        UUID familyId = familyIdOrNull(refreshToken);
        if (familyId != null) {
            store.revokeFamily(familyId, Instant.now());
        }
    }

    /** Глобальный отзыв: реакция на инцидент, будущая смена пароля. См. 10.1. */
    public void revokeAllSessions(Long userId) {
        store.revokeAllForUser(userId, Instant.now());
    }

    /**
     * Токен старого формата (выдан до релиза ротации).
     * Фаза 1 — обменять на новую сессию, не разлогинивая человека.
     * Фаза 2 — отклонить. См. раздел 5.
     */
    private Tokens handleLegacyToken(User user) {
        if (jwtProperties.isEnforceSessionClaims()) {
            throw new BadCredentialsException("Refresh token predates rotation");
        }
        log.info("Migrating legacy refresh token to a rotating session: userId={}", user.getId());
        return startNewSession(user);
    }

    private Tokens startNewSession(User user) {
        Issued issued = issue(user, UUID.randomUUID(), Instant.now());
        store.save(issued.record());
        return issued.tokens();
    }

    /**
     * Гонка честного клиента: предъявленный токен погашен только что, и его преемник
     * ещё активен — значит цепочка не разветвлялась. При краже предъявляется токен
     * из середины цепочки, чей преемник давно погашен. См. 3.5.
     */
    private boolean isBenignRace(UUID presentedJti, Instant now) {
        long grace = jwtProperties.getRefreshRaceGraceSeconds();
        if (grace <= 0) {
            return false;
        }
        return store.find(presentedJti)
                .filter(t -> t.getRevokedAt() != null)
                .filter(t -> t.getRevokedAt().isAfter(now.minusSeconds(grace)))
                .map(RefreshToken::getReplacedByJti)
                .flatMap(store::find)
                .filter(RefreshToken::isActive)
                .isPresent();
    }

    private Issued issue(User user, UUID familyId, Instant now) {
        UUID jti = UUID.randomUUID();
        Tokens tokens = new Tokens(
                jwtUtils.generateAccessToken(user, familyId),
                jwtUtils.generateRefreshToken(user, jti, familyId)
        );
        RefreshToken record = RefreshToken.builder()
                .jti(jti)
                .userId(user.getId())
                .familyId(familyId)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(jwtProperties.getRefreshTokenValiditySeconds()))
                .build();
        return new Issued(tokens, record);
    }

    private Jwt decodeOrReject(String refreshToken) {
        try {
            return jwtUtils.decodeRefresh(refreshToken);
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid refresh token", e);
        }
    }

    private User requireUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Unknown token subject"));
    }

    /** null вместо исключения: отсутствующий или битый claim — не 500, а путь к 401. */
    private UUID parseUuidOrNull(String raw) {
        if (raw == null) {
            return null;
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private UUID familyIdOrNull(String refreshToken) {
        try {
            return parseUuidOrNull(jwtUtils.decodeRefresh(refreshToken)
                    .getClaimAsString(JWTUtils.CLAIM_FAMILY_ID));
        } catch (JwtException e) {
            return null;
        }
    }

    private record Issued(Tokens tokens, RefreshToken record) {
    }

    public record Tokens(String access, String refresh) {
    }

    /**
     * Выданные токены вместе с тем, кому они выданы. Субъект возвращается отдельно, а не
     * полем Tokens: обновление - единственный сценарий, где вызывающий не знает субъекта
     * заранее, и журналу нужен именно тот адрес, что стоял в проверенном токене.
     *
     * @param tokens  новая пара токенов
     * @param subject владелец предъявленного refresh-токена
     */
    public record RefreshResult(Tokens tokens, String subject) {
    }
}
