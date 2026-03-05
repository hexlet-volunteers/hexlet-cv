package io.hexlet.cv.util;

import io.hexlet.cv.repository.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final PasswordResetTokenRepository tokenRepository;

    // Очистка каждый час
    @Scheduled(fixedRate = 3600000)
    public void cleanUp() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteAllByExpiryDateBefore(now);
    }
}
