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
}
