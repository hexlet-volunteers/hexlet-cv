package io.hexlet.cv.repository;

import io.hexlet.cv.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserId(Long userId);
    boolean existsByToken(String token);

    void deleteByUserId(Long userId);
    void deleteByToken(String token);
    void deleteAllByExpiryDateBefore(LocalDateTime date);

    List<PasswordResetToken> findAllByExpiryDateBefore(LocalDateTime date);
    long countAllByExpiryDateBefore(LocalDateTime date);

    @Query("SELECT t FROM PasswordResetToken t JOIN FETCH t.user WHERE t.token = :token")
    Optional<PasswordResetToken> findByTokenWithUser(@Param("token") String token);

    @Modifying
    @Query("DELETE FROM PasswordResetToken t WHERE t.expiryDate < :date")
    int deleteExpiredTokens(@Param("date") LocalDateTime date);
}
