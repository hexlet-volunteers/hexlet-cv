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
import io.hexlet.cv.service.PasswordGeneratorService;
import io.hexlet.cv.service.PasswordResetService;
import io.hexlet.cv.util.I18n;
import io.hexlet.cv.validator.PasswordResetValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final I18n i18n;
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordResetValidator passwordResetValidator;

    @GetMapping("/auth/forgot")
    public ResponseEntity<?> showForgotPage(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        props.put("form", new EmailFormDTO());
        return inertia.render("Auth/Forgot", props);
    }

    @PostMapping("/auth/forgot")
    public ResponseEntity<?> processForgot(@Valid @RequestBody EmailFormDTO form,
                                           HttpServletRequest request) {

        if (passwordResetService.userExists(form.getEmail())) {
            String resetUrl = generateResetUrl(request, form.getEmail());
            emailService.sendResetEmail(form.getEmail(), resetUrl);
        }

        return createSuccessResponse(request, "password.reset.email_sent");
    }

    @GetMapping("/auth/reset")
    public ResponseEntity<?> showResetPage(@RequestParam String token, HttpServletRequest request) {
        boolean isValid = passwordResetService.isTokenValid(token);
        var props = flashPropsService.buildProps(request);

        props.put("token", token);
        props.put("isValid", isValid);
        props.put("form", new PasswordResetDTO());

        if (!isValid) {
            props.put("errors", Map.of("token", i18n.get("password.reset.token.invalid")));
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

        User user = passwordResetService.getUserByToken(token);

        passwordResetValidator.validate(form, user.getFirstName(), user.getLastName());

        String passwordToSet = form.isAutoGenerate()
                ? passwordGeneratorService.generateStrongPassword()
                : form.getPassword();

        PasswordResetResponseDTO resetResult = passwordResetService.resetPassword(token, passwordToSet);

        if (form.isAutoGenerate()) {
            emailService.sendNewPasswordEmail(resetResult.getEmail(), passwordToSet);
        }

        return createSuccessResponse(request, "password.reset.success");
    }

    private ResponseEntity<?> createSuccessResponse(HttpServletRequest request, String messageKey) {
        String successMessage = i18n.get(messageKey);

        request.getSession().setAttribute("flash", Map.of("success", successMessage));
        return inertia.redirect("/users/sign_in");
    }

    private ResponseEntity<?> handleInvalidToken(String token,
                                                 PasswordResetDTO form,
                                                 HttpServletRequest request) {
        passwordResetTokenRepository.deleteByToken(token);

        var props = flashPropsService.buildProps(request);
        props.put("token", token);
        props.put("form", form);
        props.put("errors", Map.of("token", i18n.get("password.reset.token.invalid")));

        return inertia.render("Auth/Reset", props);
    }

    private String generateResetUrl(HttpServletRequest request, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ClientException("email", "User not found", HttpStatus.NOT_FOUND));

        String token = passwordResetService.createPasswordResetToken(user);

        String lang = LocaleContextHolder.getLocale().getLanguage();

        String baseUrl = String.format("%s://%s:%d",
                request.getScheme(), request.getServerName(), request.getServerPort());
        return baseUrl + "/auth/reset?token=" + token + "&lang=" + lang;
    }

}
