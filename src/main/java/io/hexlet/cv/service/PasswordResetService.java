package io.hexlet.cv.service;

import io.hexlet.cv.dto.reset.PasswordResetResponseDTO;
import io.hexlet.cv.handler.exception.InvalidTokenException;
import io.hexlet.cv.handler.exception.WeakPasswordException;
import io.hexlet.cv.mapper.PasswordResetMapper;
import io.hexlet.cv.model.PasswordResetToken;
import io.hexlet.cv.model.User;
import io.hexlet.cv.repository.PasswordResetTokenRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.validator.CommonPasswordValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonPasswordValidator commonPasswordValidator;
    private final PasswordResetMapper passwordResetMapper;

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public PasswordResetResponseDTO resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByTokenWithUser(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new InvalidTokenException("Reset token has expired");
        }

        User user = resetToken.getUser();
        if (!user.isEnabled()) {
            tokenRepository.delete(resetToken);
            throw new InvalidTokenException("User account is disabled");
        }

        if (commonPasswordValidator.isCommonPassword(newPassword)) {
            throw new WeakPasswordException("Password is too common");
        }

        user.setEncryptedPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return passwordResetMapper.toResponseDTO(user.getEmail());
    }

    @Transactional
    public String createPasswordResetToken(User user) {
        tokenRepository.deleteByUserId(user.getId());

        PasswordResetToken resetToken = PasswordResetToken.createForUser(user);
        tokenRepository.save(resetToken);

        log.info("Created password reset token for user: {}", user.getEmail());

        return resetToken.getToken();
    }

    public boolean isTokenValid(String token) {
        return tokenRepository.findByTokenWithUser(token)
                .map(resetToken ->
                        resetToken.isValid()
                                && resetToken.getUser().isEnabled()
                )
                .orElse(false);
    }

    public String getUserByToken(String token) {
        return tokenRepository.findByTokenWithUser(token)
                .filter(PasswordResetToken::isValid)
                .filter(resetToken -> resetToken.getUser().isEnabled())
                .map(PasswordResetToken::getUser)
                .map(User::getEmail)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired token"));
    }

    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        long deletedCount = tokenRepository.countAllByExpiryDateBefore(now);

        if (deletedCount > 0) {
            tokenRepository.deleteAllByExpiryDateBefore(now);
        }
    }
}
