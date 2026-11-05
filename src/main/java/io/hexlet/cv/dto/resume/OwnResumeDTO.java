package io.hexlet.cv.dto.resume;

import java.time.LocalDateTime;

// Резюме для автора: все поля формы, включая контакты, и статус публикации.
public record OwnResumeDTO(
        Long id,
        String name,
        String summary,
        String skillsDescription,
        String projectsDescription,
        String awardsDescription,
        String aboutMyself,
        String githubUrl,
        String city,
        String relocation,
        String englishFluency,
        String contactEmail,
        String contactTelegram,
        String contactPhone,
        boolean published,
        LocalDateTime updatedAt) {
}
