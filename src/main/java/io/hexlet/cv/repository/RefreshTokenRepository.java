package io.hexlet.cv.repository;

import io.hexlet.cv.model.RefreshToken;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    /**
     * Атомарно гасит токен, если он ещё активен.
     * Возвращает 1 — токен был активен и погашен; 0 — не найден либо уже погашен.
     * Единственная точка, отвечающая на вопрос «первое предъявление или повторное».
     *
     * @param jti идентификатор токена.
     * @param successorJti идентификатор токена-преемника.
     * @param now текущее время.
     * @return количество обновлённых записей.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update RefreshToken t
              set t.revokedAt = :now, t.replacedByJti = :successorJti
            where t.jti = :jti
              and t.revokedAt is null
           """)
    int revokeIfActive(@Param("jti") UUID jti,
                       @Param("successorJti") UUID successorJti,
                       @Param("now") Instant now);

    /**
     * Гасит всю цепочку: logout одной сессии либо реакция на детект кражи.
     *
     * @param familyId идентификатор семейства токенов.
     * @param now текущее время.
     * @return количество токенов в семействе, которые были активны и погашены.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update RefreshToken t
              set t.revokedAt = :now
            where t.familyId = :familyId
              and t.revokedAt is null
           """)
    int revokeFamily(@Param("familyId") UUID familyId, @Param("now") Instant now);

    /**
     * «Выйти со всех устройств» / реакция на инцидент.
     *
     * @param userId идентификатор пользователя.
     * @param now текущее время.
     * @return количество токенов пользователя, которые были активны и погашены.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
           update RefreshToken t
              set t.revokedAt = :now
            where t.userId = :userId
              and t.revokedAt is null
           """)
    int revokeAllForUser(@Param("userId") Long userId, @Param("now") Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from RefreshToken t where t.expiresAt < :cutoff")
    int deleteExpired(@Param("cutoff") Instant cutoff);
}
