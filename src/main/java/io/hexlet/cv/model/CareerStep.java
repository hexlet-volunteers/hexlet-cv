package io.hexlet.cv.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "career_steps")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CareerStep {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text", nullable = false)
    private String description;

    @Column(columnDefinition = "text", nullable = false)
    private String tasksText;

    private Boolean reviewNeeded;

    @Column(nullable = false)
    private String locale;

    private String notificationKind;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "careerStep", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CareerItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "careerStep", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CareerStepMember> members = new ArrayList<>();
}
