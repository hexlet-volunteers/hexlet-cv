package io.hexlet.cv.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SpaCsrfTokenRequestHandlerTest {

    private final SpaCsrfTokenRequestHandler handler = new SpaCsrfTokenRequestHandler();

    @Test
    void apiRequestGetsReadableStrictCookie() {
        var repository = SecurityConfig.csrfTokenRepository();
        var request = new MockHttpServletRequest("GET", "/api/auth/csrf");
        var response = new MockHttpServletResponse();

        var deferred = repository.loadDeferredToken(request, response);
        handler.handle(request, response, deferred::get);

        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE)).anySatisfy(c -> assertThat(c)
                .startsWith("XSRF-TOKEN=")
                .contains("Secure")
                .doesNotContain("HttpOnly"));
    }

    @Test
    void nonApiRequestGetsNoCookie() {
        var repository = SecurityConfig.csrfTokenRepository();
        var request = new MockHttpServletRequest("GET", "/users/sign_in");
        var response = new MockHttpServletResponse();

        var deferred = repository.loadDeferredToken(request, response);
        handler.handle(request, response, deferred::get);

        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE)).isEmpty();
    }

    @Test
    void headerTokenIsAcceptedAsIs() {
        var repository = SecurityConfig.csrfTokenRepository();
        var request = new MockHttpServletRequest("POST", "/api/auth/login");
        var token = repository.generateToken(request);
        request.addHeader(token.getHeaderName(), token.getToken());

        assertThat(handler.resolveCsrfTokenValue(request, token)).isEqualTo(token.getToken());
    }
}
