package io.hexlet.cv.controller.api;

import io.hexlet.cv.audit.AuditEventType;
import io.hexlet.cv.audit.AuditLogger;
import io.hexlet.cv.audit.AuditReason;
import io.hexlet.cv.audit.AuditSubject;
import io.hexlet.cv.audit.LoginFailureRateDetector;
import io.hexlet.cv.dto.api.CurrentUserDTO;
import io.hexlet.cv.dto.user.auth.ForgotPasswordRequestDTO;
import io.hexlet.cv.dto.user.auth.LoginRequestDTO;
import io.hexlet.cv.dto.user.auth.RegistrationRequestDTO;
import io.hexlet.cv.dto.user.auth.ResetPasswordRequestDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.handler.exception.InvalidPasswordException;
import io.hexlet.cv.handler.exception.UserAlreadyExistsException;
import io.hexlet.cv.handler.exception.UserNotFoundException;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.security.TokenCookieService;
import io.hexlet.cv.security.TokenService;
import io.hexlet.cv.service.LoginService;
import io.hexlet.cv.service.PasswordResetService;
import io.hexlet.cv.service.RegistrationService;
import io.hexlet.cv.util.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Вход, регистрация и выход для SPA. Делает то же, что Inertia-контроллеры
 * {@code /users/sign_in}, {@code /users} и {@code /users/sign_out}, но отвечает JSON,
 * а не редиректом (#1309). Токены, как и там, уходят в HttpOnly-куки.
 */
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class ApiAuthController {

    private final LoginService loginService;
    private final RegistrationService registrationService;
    private final TokenService tokenService;
    private final TokenCookieService tokenCookieService;
    private final UserRepository userRepository;
    private final UserUtils userUtils;
    private final AuditLogger auditLogger;
    private final LoginFailureRateDetector loginFailureRateDetector;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public CurrentUserDTO login(@Valid @RequestBody LoginRequestDTO dto,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        TokenService.Tokens tokens;
        try {
            loginService.login(dto);
            tokens = tokenService.authenticateAndGenerate(dto.getEmail(), dto.getPassword());
        } catch (UserNotFoundException | InvalidPasswordException | AuthenticationException e) {
            auditLogger.logFailure(AuditEventType.LOGIN, dto.getEmail(), loginFailureReason(e), request);
            loginFailureRateDetector.recordFailure(dto.getEmail(), request);
            throw e;
        }
        setCookies(response, tokens);
        auditLogger.logSuccess(AuditEventType.LOGIN, dto.getEmail(), request);
        return currentUser(dto.getEmail());
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CurrentUserDTO register(@Valid @RequestBody RegistrationRequestDTO dto,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        TokenService.Tokens tokens;
        try {
            registrationService.registration(dto);
            tokens = tokenService.authenticateAndGenerate(dto.getEmail(), dto.getPassword());
        } catch (UserAlreadyExistsException e) {
            auditLogger.logFailure(AuditEventType.REGISTRATION, dto.getEmail(), AuditReason.EMAIL_TAKEN, request);
            throw e;
        } catch (AuthenticationException e) {
            auditLogger.logFailure(AuditEventType.REGISTRATION, dto.getEmail(),
                    AuditReason.AUTHENTICATION_FAILED, request);
            throw e;
        }
        setCookies(response, tokens);
        auditLogger.logSuccess(AuditEventType.REGISTRATION, dto.getEmail(), request);
        return currentUser(dto.getEmail());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        var subject = AuditSubject.current();
        if (refreshToken != null) {
            tokenService.revokeByRefreshToken(refreshToken);
        }
        var expired = tokenCookieService.buildExpiredCookies();
        response.addHeader(HttpHeaders.SET_COOKIE, expired.access().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expired.refresh().toString());

        if (refreshToken == null) {
            auditLogger.logFailure(AuditEventType.LOGOUT, subject, AuditReason.TOKEN_MISSING, request);
        } else {
            auditLogger.logSuccess(AuditEventType.LOGOUT, subject, request);
        }
        return ResponseEntity.noContent().build();
    }

    /**
     * Запрос ссылки на сброс пароля. Ответ всегда 204, даже для незарегистрированного email.
     *
     * @param dto адрес из формы
     * @return пустой ответ
     */
    @PostMapping("/password/forgot")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        passwordResetService.requestReset(dto.getEmail());
        return ResponseEntity.noContent().build();
    }

    /**
     * Новый пароль по токену из письма.
     *
     * @param dto токен и новый пароль
     * @return 204 или 400 с ошибкой в поле {@code token}
     */
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        try {
            passwordResetService.reset(dto.getToken(), dto.getPassword());
        } catch (ClientException e) {
            return ResponseEntity.badRequest().body(Map.of("errors", Map.of("token", e.getMessage())));
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public CurrentUserDTO me() {
        return CurrentUserDTO.from(userUtils.getCurrentUser());
    }

    private void setCookies(HttpServletResponse response, TokenService.Tokens tokens) {
        var cookies = tokenCookieService.buildCookies(tokens);
        response.addHeader(HttpHeaders.SET_COOKIE, cookies.access().toString());
        response.addHeader(HttpHeaders.SET_COOKIE, cookies.refresh().toString());
    }

    private CurrentUserDTO currentUser(String email) {
        return userRepository.findByEmail(email)
                .map(CurrentUserDTO::from)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
    }

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
