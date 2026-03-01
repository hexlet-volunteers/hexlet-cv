package io.hexlet.cv.service;

import io.hexlet.cv.handler.exception.EmailSendingException;
import io.hexlet.cv.model.User;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import io.hexlet.cv.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;
    private final Environment environment;

    public void sendResetEmail(String email, String clientUrl) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        String resetToken = passwordResetService.createPasswordResetToken(user);
        String resetLink = buildResetLink(clientUrl, resetToken);

        if (isDevProfile()) {
            return;
        }

        sendEmail(email, "email.reset.subject", createResetEmailText(resetLink));
    }

    public void sendNewPasswordEmail(String email, String newPassword) {
        if (isDevProfile()) {
            return;
        }

        sendEmail(email, "email.new_password.subject", createNewPasswordEmailText(newPassword));
    }

    private void sendEmail(String to, String subjectKey, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(getMessage(subjectKey));
            message.setText(text);

            mailSender.send(message);
        } catch (MailException e) {
            throw new EmailSendingException("Failed to send email", e);
        }
    }

    private String buildResetLink(String clientUrl, String token) {
        if (clientUrl.contains("?")) {
            return clientUrl + "&token=" + token;
        } else {
            return clientUrl + "?token=" + token;
        }
    }

    private boolean isDevProfile() {
        try {
            return environment != null
                    && (Arrays.asList(environment.getActiveProfiles()).contains("dev")
                    || Arrays.asList(environment.getActiveProfiles()).contains("test"));
        } catch (Exception e) {
            String activeProfile = System.getProperty("spring.profiles.active", "");
            return activeProfile.contains("dev") || activeProfile.contains("test");
        }
    }

    private String createResetEmailText(String resetLink) {
        return getMessage("email.reset.text", resetLink);
    }

    private String createNewPasswordEmailText(String newPassword) {
        return getMessage("email.new_password.text", newPassword);
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}

