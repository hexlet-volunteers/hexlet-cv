package io.hexlet.cv.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "career_members", indexes = {
    @Index(name = "idx_cm_career_id", columnList = "career_id"),
    @Index(name = "idx_cm_user_id", columnList = "user_id"),
    @Index(name = "idx_cm_user_career_unique", columnList = "user_id,career_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CareerMember {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String state;
    private LocalDateTime finishedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "careerMember", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CareerMemberVersion> versions = new ArrayList<>();


    @OneToMany(mappedBy = "careerMember", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CareerStepMember> stepMembers = new ArrayList<>();
}
