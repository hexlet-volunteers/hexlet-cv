package io.hexlet.cv.repository;

import io.hexlet.cv.model.Resume;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
      //  Page<Resume> findByUserId(Long userId, Pageable pageable);
    Optional<Resume> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
