package io.hexlet.cv.service;

import io.hexlet.cv.handler.exception.UserNotFoundException;
import io.hexlet.cv.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import io.hexlet.cv.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    @Autowired(required = false)
    private final Environment environment;

    public void sendResetEmail(String email, String clientUrl) {
        if (!userRepository.existsByEmail(email)) {
            return;
        }

        if (isDevProfile()) {
            String resetToken = passwordResetService.createPasswordResetToken(
                    userRepository.findByEmail(email).orElseThrow()
            );
            String resetLink = clientUrl + "?token=" + resetToken;
            return;
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        String resetToken = passwordResetService.createPasswordResetToken(user);
        String resetLink = clientUrl + "?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(getMessage("email.reset.subject"));
        message.setText(createResetEmailText(resetLink));

        mailSender.send(message);
    }

    public void sendNewPasswordEmail(String email, String newPassword) {
        if (isDevProfile()) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(getMessage("email.new_password.subject"));
        message.setText(createNewPasswordEmailText(newPassword));

        mailSender.send(message);
    }

    private boolean isDevProfile() {
        try {
            return environment != null &&
                    (environment.matchesProfiles("dev") || environment.matchesProfiles("test"));
        } catch (Exception e) {
            String activeProfile = System.getProperty("spring.profiles.active", "");
            return activeProfile.contains("dev") || activeProfile.contains("test");
        }
    }

    private String createResetEmailText(String resetLink) {
        return String.format(
                getMessage("email.reset.text"),
                resetLink
        );
    }

    private String createNewPasswordEmailText(String newPassword) {
        return String.format(
                getMessage("email.new_password.text"),
                newPassword
        );
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}

