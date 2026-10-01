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
@Table(name = "career_step_members", indexes = {
    @Index(name = "idx_csm_step_id", columnList = "career_step_id"),
    @Index(name = "idx_csm_member_id", columnList = "career_member_id"),
    @Index(name = "idx_csm_unique", columnList = "career_member_id,career_step_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CareerStepMember {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "career_step_id", nullable = false)
    private CareerStep careerStep;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "career_member_id", nullable = false)
    private CareerMember careerMember;
    @Column(nullable = false)
    private String state;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;



}
