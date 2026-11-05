package io.hexlet.cv.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import io.hexlet.cv.model.enums.ApplicationEventType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Событие в таймлайне отклика: смена стадии, ответ компании, интервью, письмо, заметка.
 */
@Entity
@Table(name = "job_application_events")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class JobApplicationEvent {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationEventType type;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    @CreatedDate
    private LocalDateTime createdAt;
}
