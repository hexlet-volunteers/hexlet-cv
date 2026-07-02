package io.hexlet.cv.model;
import static jakarta.persistence.GenerationType.IDENTITY;

import io.hexlet.cv.model.converter.RoleTypeConverter;
import io.hexlet.cv.model.enums.RoleType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_confirmation_token", columnList = "confirmation_token", unique = true),
    @Index(name = "idx_users_reset_password_token", columnList = "reset_password_token", unique = true),
    @Index(name = "idx_users_unlock_token", columnList = "unlock_token", unique = true)
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String email;
    private String firstName;
    private String lastName;
    private String encryptedPassword;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordSentAt;
    private LocalDateTime rememberCreatedAt;
    private Integer signInCount;
    private LocalDateTime currentSignInAt;
    private LocalDateTime lastSignInAt;
    private String currentSignInIp;
    private String lastSignInIp;
    private String confirmationToken;
    private LocalDateTime confirmedAt;
    private LocalDateTime confirmationSentAt;
    private String unconfirmedEmail;
    private Integer failedAttempts;
    private String unlockToken;
    private LocalDateTime lockedAt;
    private String provider;
    private String uid;
    private Integer resumeAnswerLikesCount;
    private String about;
    private Boolean resumeMailEnabled;
    private Boolean bouncedEmail;
    private Boolean markedAsSpam;
    private Boolean emailDisabledDelivery;

    @Convert(converter = RoleTypeConverter.class)
    private RoleType role;

    private String state;

    private String locale;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CareerMember> careerMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Resume> resumes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeAnswer> resumeAnswers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeAnswerLike> resumeAnswerLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeAnswerComment> resumeAnswerComments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "creator", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Vacancy> vacancies = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Impression> impressions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ResumeComment> resumeComments = new ArrayList<>();

    // @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    //private List<ResumeEducation> resumeEducations = new ArrayList<>();

   // @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
   // private List<ResumeWork> resumeWorks = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private NewsletterSettings newsletterSettings;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(new SimpleGrantedAuthority("ROLE_" + this.getRole().name()));
    }

    @Override
    public String getPassword() {
        return encryptedPassword;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
