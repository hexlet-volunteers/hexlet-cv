package io.hexlet.cv.service;

import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.security.TokenService;
import io.hexlet.cv.service.mail.PasswordResetMailer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Сброс пароля по ссылке из письма (#793).
 *
 * <p>В базе хранится только SHA-256 от токена: утечка таблицы не даёт войти по чужой ссылке.
 * Ссылка живёт два часа и одноразовая. После смены пароля все сессии пользователя отзываются.
 */
@Slf4j
@Service
public class PasswordResetService {

    static final Duration TOKEN_TTL = Duration.ofHours(2);
    /** Не чаще одного письма в минуту на адрес, чтобы форму не превратили в спам-рассылку. */
    static final Duration RESEND_INTERVAL = Duration.ofMinutes(1);

    private final SecureRandom random = new SecureRandom();
    private final UserRepository userRepository;
    private final PasswordResetMailer mailer;
    private final BCryptPasswordEncoder encoder;
    private final TokenService tokenService;
    private final Clock clock;
    private final String publicUrl;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetMailer mailer,
                                BCryptPasswordEncoder encoder,
                                TokenService tokenService,
                                @Value("${app.public-url:http://localhost:5173}") String publicUrl) {
        this.userRepository = userRepository;
        this.mailer = mailer;
        this.encoder = encoder;
        this.tokenService = tokenService;
        this.clock = Clock.systemDefaultZone();
        this.publicUrl = publicUrl;
    }

    /**
     * Отправляет ссылку, если пользователь есть. Отвечает одинаково для любых адресов,
     * чтобы по форме нельзя было проверить, зарегистрирован ли email.
     *
     * @param email адрес из формы
     */
    @Transactional
    public void requestReset(String email) {
        var user = userRepository.findByEmail(email.trim().toLowerCase(Locale.ROOT))
                .or(() -> userRepository.findByEmail(email.trim()));
        if (user.isEmpty()) {
            return;
        }
        var u = user.get();
        var now = LocalDateTime.now(clock);
        if (u.getResetPasswordSentAt() != null && u.getResetPasswordSentAt().plus(RESEND_INTERVAL).isAfter(now)) {
            return;
        }
        var token = newToken();
        u.setResetPasswordToken(hash(token));
        u.setResetPasswordSentAt(now);
        userRepository.save(u);

        var link = UriComponentsBuilder.fromUriString(publicUrl)
                .path("/password/reset")
                .queryParam("token", token)
                .build()
                .toUriString();
        mailer.send(u, link);
    }

    /**
     * Ставит новый пароль по токену из ссылки.
     *
     * @param token токен из ссылки
     * @param password новый пароль
     * @throws ClientException если ссылка неверная, использована или устарела
     */
    @Transactional
    public void reset(String token, String password) {
        var user = userRepository.findByResetPasswordToken(hash(token))
                .filter(this::notExpired)
                .orElseThrow(() -> new ClientException("Ссылка устарела или уже использована. Запросите новую."));

        user.setEncryptedPassword(encoder.encode(password));
        user.setResetPasswordToken(null);
        user.setResetPasswordSentAt(null);
        userRepository.save(user);
        tokenService.revokeAllSessions(user.getId());
        log.info("Пароль сброшен по ссылке (user id {})", user.getId());
    }

    private boolean notExpired(User user) {
        var sentAt = user.getResetPasswordSentAt();
        return sentAt != null && sentAt.plus(TOKEN_TTL).isAfter(LocalDateTime.now(clock));
    }

    private String newToken() {
        var bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String hash(String token) {
        try {
            var digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
