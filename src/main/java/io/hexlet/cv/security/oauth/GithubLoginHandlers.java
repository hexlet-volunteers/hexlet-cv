package io.hexlet.cv.security.oauth;

import io.hexlet.cv.audit.AuditEventType;
import io.hexlet.cv.audit.AuditLogger;
import io.hexlet.cv.audit.AuditReason;
import io.hexlet.cv.security.TokenCookieService;
import io.hexlet.cv.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * После ответа GitHub: ставит те же HttpOnly-куки, что и вход по паролю, и уводит на фронт.
 * Ошибки возвращают на {@code /login?error=...}, где фронт показывает понятный текст.
 */
@Component
public class GithubLoginHandlers {

    private final GithubAccountService accounts;
    private final TokenService tokenService;
    private final TokenCookieService cookies;
    private final AuditLogger auditLogger;
    private final String publicUrl;
    private final ObjectProvider<ClientRegistrationRepository> registrations;

    public GithubLoginHandlers(GithubAccountService accounts, TokenService tokenService,
                               TokenCookieService cookies, AuditLogger auditLogger,
                               ObjectProvider<ClientRegistrationRepository> registrations,
                               @Value("${app.public-url:http://localhost:5173}") String publicUrl) {
        this.registrations = registrations;
        this.accounts = accounts;
        this.tokenService = tokenService;
        this.cookies = cookies;
        this.auditLogger = auditLogger;
        this.publicUrl = publicUrl.replaceAll("/+$", "");
    }

    /**
     * Подключает вход через GitHub, если настроены ключи OAuth-приложения.
     *
     * @param http цепочка фильтров безопасности
     * @throws Exception ошибка конфигурации Spring Security
     */
    public void applyTo(HttpSecurity http) throws Exception {
        var repository = registrations.getIfAvailable();
        if (repository == null) {
            return;
        }
        http.oauth2Login(oauth -> oauth
                .clientRegistrationRepository(repository)
                .userInfoEndpoint(info -> info.userService(new GithubOAuth2UserService(RestClient.create())))
                .successHandler(success())
                .failureHandler(failure()));
    }

    public AuthenticationSuccessHandler success() {
        return this::onSuccess;
    }

    public AuthenticationFailureHandler failure() {
        return (request, response, exception) -> onFailure(request, response, exception);
    }

    private void onSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
            throws IOException {
        var principal = (OAuth2User) auth.getPrincipal();
        var profile = new GithubAccountService.GithubProfile(
                Objects.toString(principal.getAttribute("id"), null),
                principal.getAttribute("login"),
                principal.getAttribute("name"),
                principal.getAttribute(GithubOAuth2UserService.VERIFIED_EMAIL));

        var result = accounts.resolve(profile);
        if (result.user().isEmpty()) {
            auditLogger.logFailure(AuditEventType.LOGIN, "github:" + profile.login(),
                    AuditReason.AUTHENTICATION_FAILED, request);
            response.sendRedirect(publicUrl + "/login?error=" + result.error());
            return;
        }

        var user = result.user().get();
        var tokens = tokenService.startSessionFor(user);
        var pair = cookies.buildCookies(tokens);
        response.addHeader(HttpHeaders.SET_COOKIE, pair.access().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, pair.refresh().toString());
        // Сессия нужна была только на время OAuth-обмена (state), дальше живём на JWT.
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        auditLogger.logSuccess(AuditEventType.LOGIN, user.getEmail(), request);
        response.sendRedirect(publicUrl + "/app/resumes");
    }

    private void onFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException {
        auditLogger.logFailure(AuditEventType.LOGIN, "github", AuditReason.AUTHENTICATION_FAILED, request);
        response.sendRedirect(publicUrl + "/login?error=github_failed");
    }
}
