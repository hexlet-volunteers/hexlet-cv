package io.hexlet.cv.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.security.cookie")
@Getter
@Setter
public class CookieProperties {

    private CookieSettings access;
    private CookieSettings refresh;

    @Getter
    @Setter
    public static class CookieSettings {
        private boolean httpOnly;
        private boolean secure;
        private String sameSite;
        private long maxAgeSeconds;
        private String path;
    }


}
