package io.hexlet.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Один выданный refresh-токен. Существование активной строки — единственный признак
 * того, что токен ещё не обменивали. Ротация гасит строку и создаёт следующую
 * с тем же familyId.
 */
@Entity
@Table(name = "refresh_tokens", indexes = {
    @Index(name = "idx_refresh_tokens_family", columnList = "family_id, revoked_at"),
    @Index(name = "idx_refresh_tokens_user", columnList = "user_id"),
    @Index(name = "idx_refresh_tokens_expires", columnList = "expires_at")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {

    /** Совпадает с claim jti токена. */
    @Id
    private UUID jti;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** Идентификатор цепочки ротаций одной сессии. Не меняется при обновлении. */
    @Column(name = "family_id", nullable = false)
    private UUID familyId;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    /** null — токен активен. Иначе момент отзыва. */
    @Column(name = "revoked_at")
    private Instant revokedAt;

    /** jti токена, выданного взамен этого. Даёт проходимую цепочку для аудита и отсечки гонок. */
    @Column(name = "replaced_by_jti")
    private UUID replacedByJti;

    public boolean isActive() {
        return revokedAt == null;
    }
}
