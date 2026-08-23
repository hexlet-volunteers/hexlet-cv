package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.security.TokenCookieService;
import io.hexlet.cv.security.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class LogoutController {

    private final TokenCookieService tokenCookieService;
    private final TokenService tokenService;
    private final Inertia inertia;

    @PostMapping("/users/sign_out")
    public ResponseEntity<String> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                     HttpServletResponse response,
                                     HttpSession session) {

        if (refreshToken != null) {
            tokenService.revokeByRefreshToken(refreshToken);
        }

        var expired = tokenCookieService.buildExpiredCookies();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.access().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expired.refresh().toString());

        session.setAttribute("flash", Map.of("success", true));

        return inertia.redirect("/");
    }
}
