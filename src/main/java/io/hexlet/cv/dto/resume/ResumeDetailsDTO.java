package io.hexlet.cv.dto.resume;

import java.time.LocalDateTime;
import java.util.List;

// Публичная страница резюме. Телефон и email не отдаются: они видны только автору.
public record ResumeDetailsDTO(
        Long id,
        String name,
        String summary,
        String skillsDescription,
        String projectsDescription,
        String awardsDescription,
        String aboutMyself,
        String githubUrl,
        String hexletUrl,
        String contactTelegram,
        String city,
        String relocation,
        String englishFluency,
        LocalDateTime updatedAt,
        AuthorDTO author,
        List<ResumeWorkDTO> works,
        List<ResumeEducationDTO> educations) {
}
