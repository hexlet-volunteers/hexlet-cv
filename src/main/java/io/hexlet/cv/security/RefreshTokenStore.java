package io.hexlet.cv.security;

import io.hexlet.cv.model.RefreshToken;
import io.hexlet.cv.repository.RefreshTokenRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Транзакционные границы для refresh_tokens.
 *
 * Вынесено в отдельный бин намеренно. TokenService при детекте повтора обязан
 * сначала закоммитить отзыв семейства, а потом бросить BadCredentialsException.
 * Если бы отзыв и бросок исключения жили в одной транзакции, unchecked-исключение
 * откатило бы UPDATE и отзыв не состоялся бы. Вызов @Transactional-метода изнутри
 * того же класса идёт мимо прокси и от этого не спасает — нужен именно другой бин.
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenStore {

    private final RefreshTokenRepository repository;

    @Transactional
    public void save(RefreshToken token) {
        repository.save(token);
    }

    /**
     * Одной транзакцией: гасит предъявленный токен и сохраняет преемника.
     * false — предъявленный токен не был активен; преемник в этом случае
     * не сохраняется и наружу не уходит.
     *
     * @param presentedJti идентификатор предъявленного токена.
     * @param successor токен-преемник, который нужно сохранить.
     * @param now текущее время.
     * @return true, если токен был активен и ротация выполнена; false — если токен уже погашен.
     */
    @Transactional
    public boolean rotate(UUID presentedJti, RefreshToken successor, Instant now) {
        if (repository.revokeIfActive(presentedJti, successor.getJti(), now) == 0) {
            return false;
        }
        repository.save(successor);
        return true;
    }

    /**
     * Отдельная транзакция: обязана закоммититься до броска исключения вызывающим.
     *
     * @param familyId идентификатор семейства токенов.
     * @param now текущее время.
     * @return количество погашенных токенов.
     */
    @Transactional
    public int revokeFamily(UUID familyId, Instant now) {
        return repository.revokeFamily(familyId, now);
    }

    @Transactional
    public int revokeAllForUser(Long userId, Instant now) {
        return repository.revokeAllForUser(userId, now);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> find(UUID jti) {
        return repository.findById(jti);
    }

    @Transactional
    public int purgeExpired(Instant cutoff) {
        return repository.deleteExpired(cutoff);
    }
}
