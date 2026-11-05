package io.hexlet.cv.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

/**
 * CSRF для SPA по рекомендации Spring Security.
 *
 * <p>Токен лежит в куке {@code XSRF-TOKEN} (её читает фронт) и приходит обратно в заголовке
 * {@code X-XSRF-TOKEN} как есть. Значение из параметра формы проверяется с XOR-маскированием
 * (защита от BREACH). Токен загружается на каждом запросе, чтобы кука появилась у браузера
 * до первого изменяющего запроса (фронт при старте запрашивает {@code /api/auth/me}).
 */
public final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
    private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
        xor.handle(request, response, csrfToken);
        // Куку выдаём только на запросах к REST API: остальным страницам она не нужна.
        if (request.getRequestURI().substring(request.getContextPath().length()).startsWith("/api/")) {
            csrfToken.get();
        }
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        var header = request.getHeader(csrfToken.getHeaderName());
        return (StringUtils.hasText(header) ? plain : xor).resolveCsrfTokenValue(request, csrfToken);
    }
}
