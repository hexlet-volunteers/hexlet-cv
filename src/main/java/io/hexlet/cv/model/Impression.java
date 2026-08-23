package io.hexlet.cv.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "impressions", indexes = {
    @Index(name = "idx_imp_user_id", columnList = "user_id"),
    @Index(name = "idx_imp_controller", columnList = "controller_name"),
    @Index(name = "idx_imp_action", columnList = "action_name"),
    @Index(name = "idx_imp_ip", columnList = "ip_address"),
    @Index(name = "idx_imp_request_hash", columnList = "request_hash"),
    @Index(name = "idx_imp_session_hash", columnList = "session_hash"),
    @Index(name = "idx_imp_impressionable", columnList = "impressionable_type,impressionable_id"),
    @Index(name = "idx_imp_message", columnList = "message")
})
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Impression {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // полиформная связь
    private String impressionableType;
    private Long impressionableId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String controllerName;
    private String actionName;
    private String viewName;
    private String requestHash;
    private String ipAddress;
    private String sessionHash;

    @Column(columnDefinition = "text")
    private String message;

    @Column(columnDefinition = "text")
    private String referrer;

    @Column(columnDefinition = "text")
    private String params;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
