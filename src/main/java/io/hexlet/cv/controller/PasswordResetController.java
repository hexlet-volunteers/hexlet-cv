package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.reset.EmailFormDTO;
import io.hexlet.cv.dto.reset.PasswordResetDTO;
import io.hexlet.cv.dto.reset.PasswordResetResponseDTO;
import io.hexlet.cv.handler.exception.UserNotFoundException;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.PasswordResetTokenRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.service.EmailService;
import io.hexlet.cv.service.FlashPropsService;
import io.hexlet.cv.service.PasswordResetService;
import io.hexlet.cv.util.PasswordGeneratorService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

    private static final Set<String> SUPPORTED_LOCALES = Set.of("en", "ru");
    private static final String DEFAULT_LOCALE = "en";


    @GetMapping("/auth/forgot")
    public ResponseEntity<?> showForgotPage(HttpServletRequest request) {
        String locale = resolveLocale(request);
        var props = flashPropsService.buildProps(locale, request);
        props.put("form", new EmailFormDTO());
        props.put("locale", locale);
        return inertia.render("Auth/Forgot", props);
    }

    @PostMapping("/auth/forgot")
    public ResponseEntity<?> processForgot(@Valid @RequestBody EmailFormDTO form,
                                           BindingResult bindingResult,
                                           HttpServletRequest request,
                                           HttpSession session) {

        String locale = resolveLocale(request);

        if (bindingResult.hasErrors()) {

            var props = flashPropsService.buildProps(locale, request);
            props.put("form", form);
            props.put("locale", locale);

            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            props.put("errors", errors);

            return inertia.render("Auth/Forgot", props);
        }

        if (passwordResetService.userExists(form.getEmail())) {
            String resetUrl = generateResetUrl(request, locale, form.getEmail());
            emailService.sendResetEmail(form.getEmail(), resetUrl);
        }

        String successMessage = messageSource.getMessage(
                "password.reset.email_sent",
                null,
                new Locale(locale)
        );

        if (isInertiaRequest(request)) {
            session.setAttribute("flash", Map.of("success", successMessage));
            return inertia.redirect("/users/sign_in");
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", successMessage
            ));
        }
    }

    @GetMapping("/auth/reset")
    public ResponseEntity<?> showResetPage(@RequestParam String token,
                                           HttpServletRequest request) {

        String locale = resolveLocale(request);
        boolean isValid = passwordResetService.isTokenValid(token);
        var props = flashPropsService.buildProps(locale, request);

        props.put("token", token);
        props.put("isValid", isValid);
        props.put("form", new PasswordResetDTO());
        props.put("locale", locale);

        if (!isValid) {
            props.put("errors", Map.of("token",
                    messageSource.getMessage("password.reset.token.invalid",
                            null, new Locale(locale))));
        }

        return inertia.render("Auth/Reset", props);
    }

    @PostMapping("/auth/reset")
    public ResponseEntity<?> processReset(@RequestParam String token,
                                          @Valid @RequestBody PasswordResetDTO form,
                                          HttpServletRequest request,
                                          HttpSession session) {

        String locale = resolveLocale(request);

        if (!passwordResetService.isTokenValid(token)) {
            var errorProps = flashPropsService.buildProps(locale, request);
            errorProps.put("token", token);
            errorProps.put("form", form);
            errorProps.put("locale", locale);
            errorProps.put("errors", Map.of("token",
                    messageSource.getMessage("password.reset.token.invalid",
                            null, new Locale(locale))));

            passwordResetTokenRepository.deleteByToken(token);

            return inertia.render("Auth/Reset", errorProps);
        }

        if (!form.isAutoGenerate() && !isPasswordValid(form.getPassword())) {
            var errorProps = flashPropsService.buildProps(locale, request);
            errorProps.put("token", token);
            errorProps.put("form", form);
            errorProps.put("locale", locale);
            errorProps.put("errors", Map.of("password",
                    messageSource.getMessage("password.reset.invalid",
                            null, new Locale(locale))));
            return inertia.render("Auth/Reset", errorProps);
        }

        String newPassword = form.isAutoGenerate()
                ? passwordGeneratorService.generateStrongPassword() : form.getPassword();

        PasswordResetResponseDTO resetResult = passwordResetService.resetPassword(token, newPassword);

        if (form.isAutoGenerate()) {
            emailService.sendNewPasswordEmail(resetResult.getEmail(), newPassword);
        }

        String successMessage = messageSource.getMessage(
                "password.reset.success",
                null,
                new Locale(locale)
        );

        if (isInertiaRequest(request)) {
            session.setAttribute("flash", Map.of("success", successMessage));
            return inertia.redirect("/users/sign_in");
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", successMessage
            ));
        }
    }

    private String resolveLocale(HttpServletRequest request) {

        var cookies = request.getCookies();
        if (cookies != null) {
            for (var cookie : cookies) {
                if ("locale".equals(cookie.getName()) || "i18next".equals(cookie.getName())) {
                    String locale = cookie.getValue();
                    if (SUPPORTED_LOCALES.contains(locale)) {
                        return locale;
                    }
                }
            }
        }

        String acceptLanguage = request.getHeader("Accept-Language");
        if (acceptLanguage != null && !acceptLanguage.isEmpty()) {
            String[] languages = acceptLanguage.split(",");
            for (String lang : languages) {
                String locale = lang.split(";")[0].trim().split("-")[0];
                if (SUPPORTED_LOCALES.contains(locale)) {
                    return locale;
                }
            }
        }

        String langParam = request.getParameter("lang");
        if (langParam != null && SUPPORTED_LOCALES.contains(langParam)) {
            return langParam;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("locale") != null) {
            String locale = (String) session.getAttribute("locale");
            if (SUPPORTED_LOCALES.contains(locale)) {
                return locale;
            }
        }

        return DEFAULT_LOCALE;
    }

    private boolean isInertiaRequest(HttpServletRequest request) {
        return request.getHeader("X-Inertia") != null;
    }

    private String generateResetUrl(HttpServletRequest request, String locale, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = passwordResetService.createPasswordResetToken(user);
        System.out.println("Token generated: " + token);

        String baseUrl = request.getScheme() + "://"
                + request.getServerName()
                + ":"
                + request.getServerPort();

        return baseUrl + "/auth/reset?token=" + token + "&lang=" + locale;
    }

    private boolean isPasswordValid(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*");
    }

}
