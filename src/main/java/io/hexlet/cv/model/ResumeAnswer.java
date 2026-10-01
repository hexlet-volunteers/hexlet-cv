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
@Table(name = "resume_answers", indexes = {
    @Index(name = "idx_resume_answers_resume_id", columnList = "resume_id"),
    @Index(name = "idx_resume_answers_user_id", columnList = "user_id"),
    @Index(name = "idx_resume_answers_user_resume_unique", columnList = "user_id,resume_id", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ResumeAnswer {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "text")
    private String content;

    private Integer likesCount;
    private String applyingState;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeAnswerLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "answer", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeAnswerComment> comments = new ArrayList<>();


}
