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
@Table(name = "career_items", indexes = {
    @Index(name = "idx_ci_career_id", columnList = "career_id"),
    @Index(name = "idx_ci_career_step_id", columnList = "career_step_id"),
    @Index(name = "idx_ci_career_step_unique", columnList = "career_id,career_step_id", unique = true),
    @Index(name = "idx_ci_career_order_unique", columnList = "career_id,\"order\"", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CareerItem {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    // 'order' - "order"
    @Column(name = "\"order\"")
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_step_id", nullable = false)
    private CareerStep careerStep;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
