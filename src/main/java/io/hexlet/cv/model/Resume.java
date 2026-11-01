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
@Table(name = "resumes", indexes = {
    @Index(name = "idx_resumes_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Resume {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String state;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String url;

    @Column(columnDefinition = "text")
    private String summary;

    @Column(columnDefinition = "text")
    private String skillsDescription;

    private String githubUrl;

    @Column(columnDefinition = "text")
    private String awardsDescription;

    private String englishFluency;
    private Integer impressionsCount;

    @Column(nullable = false)
    private Integer answersCount;

    private String hexletUrl;
    private String contact;
    private String locale;
    private String city;
    private String relocation;
    private String contactPhone;
    private String contactEmail;
    private String contactTelegram;
    private Boolean evaluatedAi;
    private String evaluatedAiState;

    @Column(columnDefinition = "text")
    private String projectsDescription;

    @Column(columnDefinition = "text")
    private String aboutMyself;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @OneToMany(mappedBy = "resume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeAnswer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeEducation> educations = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeWork> works = new ArrayList<>();


}
