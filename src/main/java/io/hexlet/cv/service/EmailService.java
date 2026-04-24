package io.hexlet.cv.service;

import io.hexlet.cv.handler.exception.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final MessageSource messageSource;

    public void sendResetEmail(String email, String resetLink) {
        String text = getMessage(
                "email.reset.text",
                resetLink
        );

        sendEmail(email, "email.reset.subject", text);
    }

    public void sendNewPasswordEmail(String email, String newPassword) {
        String text = getMessage(
                "email.new_password.text",
                newPassword
        );

        sendEmail(email, "email.new_password.subject", text);
    }

    private void sendEmail(String to, String subjectKey, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(getMessage(subjectKey));
            message.setText(text);

            mailSender.send(message);
            log.info("Email sent to: {}", to);
        } catch (MailException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new ServerException("Failed to send email", "EMAIL_SEND_FAILED", e);
        }
    }

    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
