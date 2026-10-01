package io.hexlet.cv.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
public class JwtProperties {
    private long accessTokenValiditySeconds;
    private long refreshTokenValiditySeconds;
    private String issuer;
    private String audience;

    /**
     * Фаза 2 релиза: требовать jti/familyId. Пока false — токены старого формата
     * принимаются и мягко переводятся на новую схему. См. раздел 5.
     */
    private boolean enforceSessionClaims = false;

    /**
     * Окно, в котором повторное предъявление только что обменянного токена
     * считается гонкой честного клиента, а не кражей. 0 — строгое поведение. См. 3.5.
     */
    private long refreshRaceGraceSeconds = 0;

    /** Расписание очистки просроченных строк refresh_tokens. */
    private String cleanupCron = "0 17 3 * * *";
}
