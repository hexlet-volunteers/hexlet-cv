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
@Table(name = "vacancies", indexes = {
    @Index(name = "idx_vacancy_country_id", columnList = "country_id"),
    @Index(name = "idx_vacancy_creator_id", columnList = "creator_id"),
    @Index(name = "idx_vacancy_external_id", columnList = "external_id")
})
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Vacancy {
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    private String state;
    private String title;
    private String programmingLanguage;
    private String location;
    private String companyName;
    private String site;
    private String contactName;
    private String contactTelegram;
    private String contactPhone;

    @Column(columnDefinition = "text")
    private String conditionsDescription;

    private String cityName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;

    private String linkForContact;
    private String contactEmail;
    private Integer salaryFrom;
    private Integer salaryTo;
    private String employmentType;
    private String positionLevel;
    private String salaryCurrency;
    private String salaryAmountType;
    private String locationOfPosition;

    @Column(columnDefinition = "text")
    private String responsibilitiesDescription;

    @Column(columnDefinition = "text")
    private String requirementsDescription;

    @Column(columnDefinition = "text")
    private String aboutCompany;

    @Column(columnDefinition = "text")
    private String aboutProject;

    @Column(columnDefinition = "text")
    private String experienceDescription;

    private String locale;
    private LocalDateTime publishedAt;
    private Long externalId;

    @Column(nullable = false)
    private String kind;

    private String cancelationReason;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
