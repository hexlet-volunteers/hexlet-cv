package io.hexlet.cv.service;

import io.hexlet.cv.converter.UserConverter;
import io.hexlet.cv.dto.reset.PasswordResetResponseDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.model.PasswordResetToken;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.PasswordResetTokenRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.util.TokenGenerator;
import io.hexlet.cv.validator.CommonPasswordValidator;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonPasswordValidator commonPasswordValidator;
    private final TokenGenerator tokenGenerator;
    private final UserConverter passwordResetConverter;

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public PasswordResetResponseDTO resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByTokenWithUser(token)
                .orElseThrow(() -> {
                    log.warn("Reset attempt with invalid token: {}", token);
                    return new ClientException("token", "Invalid reset token", HttpStatus.BAD_REQUEST);
                });

        if (resetToken.isExpired()) {
            log.warn("Expired token used for user: {}", resetToken.getUser().getEmail());
            tokenRepository.delete(resetToken);
            throw new ClientException("token", "Reset token has expired", HttpStatus.BAD_REQUEST);
        }

        User user = resetToken.getUser();
        if (!user.isEnabled()) {
            log.warn("Disabled user attempted password reset: {}", user.getEmail());
            tokenRepository.delete(resetToken);
            throw new ClientException("email", "User account is disabled", HttpStatus.FORBIDDEN);
        }

        if (commonPasswordValidator.isCommonPassword(newPassword)) {
            log.warn("Common password attempt for user: {}", user.getEmail());
            throw new ClientException("password", "Password is too common. Please choose a more secure password",
                    HttpStatus.BAD_REQUEST);
        }

        user.setEncryptedPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        tokenRepository.delete(resetToken);
        log.info("Password successfully reset for user: {}", user.getEmail());

        return passwordResetConverter.toResponseDTO(user.getEmail());
    }

    @Transactional
    public String createPasswordResetToken(User user) {

        tokenRepository.deleteByUserId(user.getId());
        String tokenValue = tokenGenerator.generateUuidToken();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(tokenValue)
                .user(user)
                .build();

        tokenRepository.save(resetToken);
        log.info("Created password reset token for user: {} | TOKEN: {}", user.getEmail(), tokenValue);

        return tokenValue;
    }

    public boolean isTokenValid(String token) {
        return tokenRepository.findByTokenWithUser(token)
                .map(resetToken ->
                        resetToken.isValid() && resetToken.getUser().isEnabled()
                )
                .orElse(false);
    }

    public String getUserEmailByToken(String token) {
        return tokenRepository.findByTokenWithUser(token)
                .filter(PasswordResetToken::isValid)
                .filter(resetToken -> resetToken.getUser().isEnabled())
                .map(PasswordResetToken::getUser)
                .map(User::getEmail)
                .orElseThrow(() -> {
                    log.warn("Invalid or expired token accessed: {}", token);
                    return new ClientException("token", "Invalid or expired token", HttpStatus.BAD_REQUEST);
                });
    }

    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        long deletedCount = tokenRepository.countAllByExpiryDateBefore(now);

        if (deletedCount > 0) {
            tokenRepository.deleteAllByExpiryDateBefore(now);
            log.info("Cleaned up {} expired password reset tokens", deletedCount);
        }
    }

    public User getUserByToken(String token) {
        return tokenRepository.findByTokenWithUser(token)
                .filter(PasswordResetToken::isValid)
                .filter(resetToken -> resetToken.getUser().isEnabled())
                .map(PasswordResetToken::getUser)
                .orElseThrow(() -> new ClientException("token", "Invalid or expired token", HttpStatus.BAD_REQUEST));
    }
}
