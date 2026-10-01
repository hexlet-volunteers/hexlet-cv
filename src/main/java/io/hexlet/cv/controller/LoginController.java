package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.audit.AuditEventType;
import io.hexlet.cv.audit.AuditLogger;
import io.hexlet.cv.audit.AuditReason;
import io.hexlet.cv.audit.LoginFailureRateDetector;
import io.hexlet.cv.dto.user.auth.LoginRequestDTO;
import io.hexlet.cv.handler.exception.InvalidPasswordException;
import io.hexlet.cv.handler.exception.UserNotFoundException;
import io.hexlet.cv.security.TokenCookieService;
import io.hexlet.cv.security.TokenService;
import io.hexlet.cv.service.FlashPropsService;
import io.hexlet.cv.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@AllArgsConstructor
public class LoginController {

    private final Inertia inertia;
    private final LoginService loginService;
    private final TokenService tokenService;
    private final FlashPropsService flashPropsService;
    private final TokenCookieService tokenCookieService;
    private final AuditLogger auditLogger;
    private final LoginFailureRateDetector loginFailureRateDetector;

    @GetMapping("/users/sign_in")
    public ResponseEntity<?> signInForm(HttpServletRequest request) {

        var props = flashPropsService.buildProps(request);

        return inertia.render("Users/SignIn/Index", props);
    }

    @PostMapping(path = "/users/sign_in")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response,
                                   HttpSession session) {

        TokenService.Tokens tokens;
        try {
            loginService.login(loginDTO);
            tokens = tokenService.authenticateAndGenerate(
                    loginDTO.getEmail(),
                    loginDTO.getPassword()
            );
        } catch (UserNotFoundException | InvalidPasswordException | AuthenticationException e) {
            auditLogger.logFailure(AuditEventType.LOGIN, loginDTO.getEmail(), loginFailureReason(e), request);
            loginFailureRateDetector.recordFailure(loginDTO.getEmail(), request);
            throw e;
        }

        var cookies = tokenCookieService.buildCookies(tokens);

        response.addHeader(HttpHeaders.SET_COOKIE, cookies.access().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookies.refresh().toString());

        session.setAttribute("flash", Map.of("success", true));
        auditLogger.logSuccess(AuditEventType.LOGIN, loginDTO.getEmail(), request);

        return inertia.redirect("/dashboard");
    }

    /**
     * Различает поводы для отказа во входе.
     *
     * @param e исключение, прервавшее попытку входа
     * @return причина отказа для записи в журнал
     */
    private static AuditReason loginFailureReason(RuntimeException e) {
        if (e instanceof UserNotFoundException) {
            return AuditReason.USER_NOT_FOUND;
        }
        if (e instanceof InvalidPasswordException) {
            return AuditReason.INVALID_PASSWORD;
        }
        return AuditReason.AUTHENTICATION_FAILED;
    }
}
