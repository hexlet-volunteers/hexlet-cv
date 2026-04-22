package io.hexlet.cv.repository;

import io.hexlet.cv.model.NewsletterSettings;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsletterSettingsRepository extends JpaRepository<NewsletterSettings, Long> {

    Optional<NewsletterSettings> findByUserId(Long userId);
}
