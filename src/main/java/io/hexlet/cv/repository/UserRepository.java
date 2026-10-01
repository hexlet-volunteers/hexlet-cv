package io.hexlet.cv.repository;

import io.hexlet.cv.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update User u set u.tokenVersion = u.tokenVersion + 1 where u.email = :email")
    void incrementTokenVersion(@Param("email") String email);

    /**
     * Валидна ли сессия: пользователь существует, tokenVersion совпадает,
     * и в семействе есть хотя бы один неотозванный refresh-токен.
     * Одним запросом — вызывается на каждом запросе с access-cookie.
     */
    @Query("""
           select count(u.id) > 0 from User u
            where u.email = :email
              and u.tokenVersion = :tokenVersion
              and exists (select 1 from RefreshToken t
                           where t.familyId = :familyId
                             and t.userId = u.id
                             and t.revokedAt is null)
           """)
    boolean isSessionValid(@Param("email") String email,
                           @Param("tokenVersion") long tokenVersion,
                           @Param("familyId") UUID familyId);
}
