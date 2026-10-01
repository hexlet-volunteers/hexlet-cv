package io.hexlet.cv.model;

import static jakarta.persistence.GenerationType.IDENTITY;

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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "taggings", indexes = {
    @Index(name = "idx_tg_tag_id", columnList = "tag_id"),
    @Index(name = "idx_tg_taggable_id", columnList = "taggable_id"),
    @Index(name = "idx_tg_taggable_type", columnList = "taggable_type"),
    @Index(name = "idx_tg_tagger_id", columnList = "tagger_id"),
    @Index(name = "idx_tg_context", columnList = "context"),
    @Index(name = "idx_tg_tenant", columnList = "tenant")
})
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Tagging {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private String taggableType;
    private Long taggableId;

    private String taggerType;
    private Long taggerId;
    private String context;
    private String tenant;

    @CreatedDate
    private LocalDateTime createdAt;
}
