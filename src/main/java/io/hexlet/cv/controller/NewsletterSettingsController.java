package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.NewsletterSettingsDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.service.FlashPropsService;
import io.hexlet.cv.service.NewsletterSettingsService;
import io.hexlet.cv.util.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping
@Slf4j
public class NewsletterSettingsController {

    private final NewsletterSettingsService newsletterSettingsService;
    private final Inertia inertia;
    private final FlashPropsService flashPropsService;
    private final MessageSource messageSource;
    private final UserUtils userUtils;

    private static final Set<String> SUPPORTED_LOCALES = Set.of("en", "ru");
    private static final String DEFAULT_LOCALE = "en";

    @GetMapping("/{locale}/account/newsletters/edit")
    public ResponseEntity<?> showEditPage(@PathVariable String locale,
                                          HttpServletRequest request) {

        try {
            userUtils.getCurrentUser(); // Проверяем аутентификацию
        } catch (ClientException e) {
            return inertia.redirect("/" + locale + "/users/sign_in");
        }


        String resolvedLocale = resolveLocale(locale, request);
        NewsletterSettingsDTO settings = newsletterSettingsService.getCurrentUserSettings();

        var props = flashPropsService.buildProps(resolvedLocale, request);
        props.put("settings", settings);
        props.put("form", settings);
        props.put("locale", resolvedLocale);

        return inertia.render("Account/Newsletters/Edit", props);
    }

    @PostMapping("/{locale}/account/newsletters/edit")
    public ResponseEntity<?> updateSettings(@PathVariable String locale,
                                            @Valid @RequestBody NewsletterSettingsDTO form,
                                            BindingResult bindingResult,
                                            HttpServletRequest request,
                                            HttpSession session) {

        try {
            userUtils.getCurrentUser();
        } catch (ClientException e) {
            return inertia.redirect("/" + locale + "/users/sign_in");
        }

        String resolvedLocale = resolveLocale(locale, request);

        if (bindingResult.hasErrors()) {
            var props = flashPropsService.buildProps(resolvedLocale, request);
            props.put("form", form);
            props.put("locale", resolvedLocale);

            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage()));
            props.put("errors", errors);

            return inertia.render("Account/Newsletters/Edit", props);
        }

        newsletterSettingsService.updateCurrentUserSettings(form);

        String successMessage = messageSource.getMessage(
                "newsletter.settings.updated",
                null,
                new Locale(resolvedLocale)
        );

        if (isInertiaRequest(request)) {
            session.setAttribute("flash", Map.of("success", successMessage));
            return inertia.redirect("/" + resolvedLocale + "/account/newsletters/edit");
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", successMessage,
                    "settings", form
            ));
        }
    }

    private String resolveLocale(String pathLocale, HttpServletRequest request) {

        if (pathLocale != null && SUPPORTED_LOCALES.contains(pathLocale)) {
            return pathLocale;
        }

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
}
