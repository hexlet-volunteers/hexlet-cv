package io.hexlet.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "newsletter_settings",
        uniqueConstraints = @UniqueConstraint(columnNames = "user_id"))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class NewsletterSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(name = "new_courses", nullable = false)
    private Boolean newCourses = true;

    @Builder.Default
    @Column(name = "course_updates", nullable = false)
    private Boolean courseUpdates = true;

    @Builder.Default
    @Column(name = "promotions", nullable = false)
    private Boolean promotions = false;

    @Builder.Default
    @Column(name = "achievements", nullable = false)
    private Boolean achievements = true;

    @Builder.Default
    @Column(name = "comments_replies", nullable = false)
    private Boolean commentsReplies = true;

    @Builder.Default
    @Column(name = "resume_views", nullable = false)
    private Boolean resumeViews = true;

    @Builder.Default
    @Column(name = "vacancy_matches", nullable = false)
    private Boolean vacancyMatches = true;

    @Builder.Default
    @Column(name = "community_news", nullable = false)
    private Boolean communityNews = false;

    @Builder.Default
    @Column(name = "marketing_tips", nullable = false)
    private Boolean marketingTips = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
