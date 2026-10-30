package io.hexlet.cv.support;

import io.hexlet.cv.config.JwtProperties;
import io.hexlet.cv.security.TokenService;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

/**
 * Выдаёт токены через TokenService — то есть с реальной сессией в refresh_tokens.
 * Заменяет прямые вызовы jwtUtils.generate*Token(...) в тестах.
 */
@Component
@RequiredArgsConstructor
public class TokenTestHelper {

    private final TokenService tokenService;
    private final JwtEncoder encoder;
    private final JwtProperties jwtProperties;

    public TokenService.Tokens issue(String email, String password) {
        return tokenService.authenticateAndGenerate(email, password);
    }

    public String accessToken(String email, String password) {
        return issue(email, password).access();
    }

    public String refreshToken(String email, String password) {
        return issue(email, password).refresh();
    }

    /**
     * Refresh-токен старого формата (без jti/familyId) для тестов миграции.
     *
     * @param email email пользователя.
     * @param tokenVersion версия токена.
     * @return строку refresh-токена старого формата (без jti/familyId).
     */
    public String legacyRefreshToken(String email, long tokenVersion) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.getIssuer())
                .audience(List.of(jwtProperties.getAudience()))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(3600))
                .subject(email)
                .claim("type", "refresh")
                .claim("tokenVersion", tokenVersion)
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
