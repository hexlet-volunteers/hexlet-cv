package io.hexlet.cv.repository;

import io.hexlet.cv.model.Resume;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long userId);
      //  Page<Resume> findByUserId(Long userId, Pageable pageable);
    Optional<Resume> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    // Каталог резюме: поиск по названию и городу среди резюме в нужном статусе.
    // Пустая строка `q` отключает фильтр.
    @EntityGraph(attributePaths = "user")
    @Query(value = """
            select r from Resume r
            where r.state = :state
              and (:q = '' or lower(r.name) like concat('%', :q, '%')
                   or lower(r.city) like concat('%', :q, '%'))
            """,
            countQuery = """
            select count(r) from Resume r
            where r.state = :state
              and (:q = '' or lower(r.name) like concat('%', :q, '%')
                   or lower(r.city) like concat('%', :q, '%'))
            """)
    Page<Resume> search(@Param("state") String state, @Param("q") String q, Pageable pageable);

    @EntityGraph(attributePaths = "user")
    Optional<Resume> findByIdAndState(Long id, String state);

    @EntityGraph(attributePaths = "user")
    List<Resume> findByUserIdAndStateOrderByUpdatedAtDesc(Long userId, String state);

    List<Resume> findByUserIdOrderByUpdatedAtDesc(Long userId);

    Optional<Resume> findByIdAndUserId(Long id, Long userId);
}
