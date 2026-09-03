package io.hexlet.cv.repository;

import io.hexlet.cv.model.StoriesStory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoriesStoryRepository extends JpaRepository<StoriesStory, Long> {

    Page<StoriesStory> findByIsPublishedTrue(Pageable pageable);
}
