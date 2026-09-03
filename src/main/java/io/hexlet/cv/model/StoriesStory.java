package io.hexlet.cv.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "stories_stories")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StoriesStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    @Comment("Идентификатор истории успеха")
    private Long id;

    @Column(name = "author_name", nullable = false)
    @Comment("Имя автора истории успеха")
    private String authorName;

    @Column(name = "avatar_url")
    @Comment("URL аватара автора истории успеха")
    private String avatarUrl;

    @Column(name = "company_name")
    @Comment("Название компании")
    private String companyName;

    @Column(name = "offer_position")
    @Comment("Должность на которую получен оффер")
    private String offerPosition;

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    @Comment("Текст истории успеха")
    private String text;

    @Column(name = "display_order")
    @Comment("Порядок отображения истории успеха")
    private Integer displayOrder;

    @Builder.Default
    @Column(name = "is_published", nullable = false, columnDefinition = "boolean default false")
    @Comment("Признак публикации истории успеха")
    private Boolean isPublished = false;

}