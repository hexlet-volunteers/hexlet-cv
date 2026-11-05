package io.hexlet.cv.repository;

import io.hexlet.cv.model.JobApplication;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {

    @EntityGraph(attributePaths = "resume")
    List<JobApplication> findByUserIdOrderByLastEventAtDesc(Long userId);

    @EntityGraph(attributePaths = {"resume", "events"})
    Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
}
