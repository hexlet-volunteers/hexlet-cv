package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.NewsletterSettingsDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.service.FlashPropsService;
import io.hexlet.cv.service.NewsletterSettingsService;
import io.hexlet.cv.util.UserUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/account/newsletters/edit")
    public ResponseEntity<?> showEditPage(HttpServletRequest request) {

        try {
            userUtils.getCurrentUser();
        } catch (ClientException e) {
            return inertia.redirect("/users/sign_in");
        }

        NewsletterSettingsDTO settings = newsletterSettingsService.getCurrentUserSettings();

        var props = flashPropsService.buildProps(request);
        props.put("settings", settings);
        props.put("form", settings);

        return inertia.render("Account/Newsletters/Edit", props);
    }

    @PostMapping("/account/newsletters/edit")
    public ResponseEntity<?> updateSettings(@Valid @RequestBody NewsletterSettingsDTO form,
                                            Locale locale) {


        newsletterSettingsService.updateCurrentUserSettings(form);

        String successMessage = messageSource.getMessage(
                "newsletter.settings.updated",
                null,
                locale
        );

        return inertia.redirect("/account/newsletters/edit");
    }
}
