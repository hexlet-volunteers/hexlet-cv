package io.hexlet.cv.security;

import io.hexlet.cv.config.CookieProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class  TokenCookieService {

    private final CookieProperties cookieProperties;


    public ResponseCookie buildExpiredAccessCookie() {
        var props = cookieProperties.getAccess();
        return ResponseCookie.from("access_token", "delete")
                .httpOnly(props.isHttpOnly())
                .secure(props.isSecure())
                .path(props.getPath())
                .maxAge(0)
                .sameSite(props.getSameSite())
                .build();
    }

    public ResponseCookie buildExpiredRefreshCookie() {
        var props = cookieProperties.getRefresh();
        return ResponseCookie.from("refresh_token", "delete")
                .httpOnly(props.isHttpOnly())
                .secure(props.isSecure())
                .path(props.getPath())
                .maxAge(0)
                .sameSite(props.getSameSite())
                .build();
    }

    public ResponseCookie buildAccessCookie(String token) {
        var props = cookieProperties.getAccess();
        return ResponseCookie.from("access_token", token)
                .httpOnly(props.isHttpOnly())
                .secure(props.isSecure())
                .path(props.getPath())
                .maxAge(props.getMaxAgeSeconds())
                .sameSite(props.getSameSite())
                .build();
    }

    public ResponseCookie buildRefreshCookie(String token) {
        var props = cookieProperties.getRefresh();
        return ResponseCookie.from("refresh_token", token)
                .httpOnly(props.isHttpOnly())
                .secure(props.isSecure())
                .path(props.getPath())
                .maxAge(props.getMaxAgeSeconds())
                .sameSite(props.getSameSite())
                .build();
    }

    public Cookies buildCookies(TokenService.Tokens tokens) {
        return new Cookies(
                buildAccessCookie(tokens.access()),
                buildRefreshCookie(tokens.refresh())
        );
    }

    public Cookies buildExpiredCookies() {
        return new Cookies(buildExpiredAccessCookie(), buildExpiredRefreshCookie());
    }

    public record Cookies(ResponseCookie access, ResponseCookie refresh) {

    }
}
