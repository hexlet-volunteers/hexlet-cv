package io.hexlet.cv.service.mail;

import io.hexlet.cv.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Письмо уходит через SMTP, если настроен {@code spring.mail.host}.
 *
 * <p>Без SMTP письмо не отправляется. В dev-профиле ссылка пишется в лог
 * ({@code app.mail.log-links=true}), чтобы сброс можно было проверить локально;
 * в проде ссылку в лог не пишем — она даёт доступ к аккаунту.
 */
@Slf4j
@Component
public class DefaultPasswordResetMailer implements PasswordResetMailer {

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String from;
    private final boolean logLinks;

    public DefaultPasswordResetMailer(ObjectProvider<JavaMailSender> mailSender,
                                      @Value("${app.mail.from:noreply@hexlet.io}") String from,
                                      @Value("${app.mail.log-links:false}") boolean logLinks) {
        this.mailSender = mailSender;
        this.from = from;
        this.logLinks = logLinks;
    }

    @Override
    public void send(User user, String link) {
        var sender = mailSender.getIfAvailable();
        if (sender == null) {
            if (logLinks) {
                log.info("SMTP не настроен, ссылка для сброса пароля {}: {}", user.getEmail(), link);
            } else {
                log.error("SMTP не настроен: письмо для сброса пароля не отправлено (user id {})", user.getId());
            }
            return;
        }
        var message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(user.getEmail());
        message.setSubject("Восстановление пароля — Хекслет Карьера");
        message.setText("""
                Здравствуйте!

                Кто-то запросил сброс пароля для вашего аккаунта в Хекслет Карьере.
                Чтобы задать новый пароль, откройте ссылку в течение двух часов:

                %s

                Если вы не запрашивали сброс, просто проигнорируйте это письмо.
                """.formatted(link));
        sender.send(message);
    }
}
