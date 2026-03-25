package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.reset.EmailFormDTO;
import io.hexlet.cv.dto.reset.PasswordResetDTO;
import io.hexlet.cv.dto.reset.PasswordResetResponseDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.PasswordResetTokenRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.service.EmailService;
import io.hexlet.cv.service.FlashPropsService;
import io.hexlet.cv.service.PasswordResetService;
import io.hexlet.cv.service.PasswordValidationService;
import io.hexlet.cv.util.PasswordGeneratorService;
import io.hexlet.cv.validator.PasswordResetValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class PasswordResetController {

    private final PasswordResetService passwordResetService;
    private final PasswordGeneratorService passwordGeneratorService;
    private final EmailService emailService;
    private final Inertia inertia;
    private final FlashPropsService flashPropsService;
    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordValidationService passwordValidationService;
    private final PasswordResetValidator passwordResetValidator;

    @GetMapping("/auth/forgot")
    public ResponseEntity<?> showForgotPage(HttpServletRequest request) {

        var props = flashPropsService.buildProps(request);
        props.put("form", new EmailFormDTO());
        return inertia.render("Auth/Forgot", props);
    }

    @PostMapping("/auth/forgot")
    public ResponseEntity<?> processForgot(@Valid @RequestBody EmailFormDTO form,
                                           HttpServletRequest request,
                                           HttpSession session) {

        passwordValidationService.validateEmail(form.getEmail());

        if (passwordResetService.userExists(form.getEmail())) {
            String resetUrl = generateResetUrl(request, form.getEmail());
            emailService.sendResetEmail(form.getEmail(), resetUrl);
        }

        return createSuccessResponse(request, session, "password.reset.email_sent");
    }

    @GetMapping("/auth/reset")
    public ResponseEntity<?> showResetPage(@RequestParam String token,
                                           HttpServletRequest request) {
        boolean isValid = passwordResetService.isTokenValid(token);
        var props = flashPropsService.buildProps(request);

        props.put("token", token);
        props.put("isValid", isValid);
        props.put("form", new PasswordResetDTO());

        if (!isValid) {
            props.put("errors", Map.of("token", getMessage("password.reset.token.invalid")));
            passwordResetTokenRepository.deleteByToken(token);
        }

        return inertia.render("Auth/Reset", props);
    }

    @PostMapping("/auth/reset")
    public ResponseEntity<?> processReset(@RequestParam String token,
                                          @Valid @RequestBody PasswordResetDTO form,
                                          HttpServletRequest request,
                                          HttpSession session) {

        if (!passwordResetService.isTokenValid(token)) {
            return handleInvalidToken(token, form, request);
        }

        passwordResetValidator.validate(form);

        String passwordToSet = form.isAutoGenerate()
                ? passwordGeneratorService.generateStrongPassword()
                : form.getPassword();

        PasswordResetResponseDTO resetResult = passwordResetService.resetPassword(token, passwordToSet);

        if (form.isAutoGenerate()) {
            emailService.sendNewPasswordEmail(resetResult.getEmail(), passwordToSet);
        }

        return createSuccessResponse(request, session, "password.reset.success");
    }

    private ResponseEntity<?> createSuccessResponse(HttpServletRequest request,
                                                    HttpSession session,
                                                    String messageKey) {
        String successMessage = getMessage(messageKey);

        if (isInertiaRequest(request)) {
            session.setAttribute("flash", Map.of("success", successMessage));
            return inertia.redirect("/users/sign_in");
        }

        return ResponseEntity.ok(new SuccessResponse(successMessage));
    }

    private ResponseEntity<?> handleInvalidToken(String token,
                                                 PasswordResetDTO form,
                                                 HttpServletRequest request) {
        passwordResetTokenRepository.deleteByToken(token);

        var props = flashPropsService.buildProps(request);
        props.put("token", token);
        props.put("form", form);
        props.put("errors", Map.of("token", getMessage("password.reset.token.invalid")));

        return inertia.render("Auth/Reset", props);
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null,
                org.springframework.context.i18n.LocaleContextHolder.getLocale());
    }

    private boolean isInertiaRequest(HttpServletRequest request) {
        return "true".equals(request.getHeader("X-Inertia"));
    }

    private String generateResetUrl(HttpServletRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ClientException("email", "User not found", HttpStatus.NOT_FOUND));

        String token = passwordResetService.createPasswordResetToken(user);

        log.debug("Generated reset token for email {}: {}", email, token);

        String baseUrl = request.getScheme() + "://"
                + request.getServerName()
                + ":"
                + request.getServerPort();
        // Подставляем текущую локаль в URL
        String lang = org.springframework.context.i18n.LocaleContextHolder.getLocale().getLanguage();
        return baseUrl + "/auth/reset?token=" + token + "&lang=" + lang;
    }

    public record SuccessResponse(boolean success, String message) {
        public SuccessResponse(String message) {
            this(true, message);
        }
    }
}
