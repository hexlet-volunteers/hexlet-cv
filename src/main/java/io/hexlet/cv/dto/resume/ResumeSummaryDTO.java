package io.hexlet.cv.dto.resume;

import java.time.LocalDateTime;

// Карточка резюме в каталоге.
public record ResumeSummaryDTO(
        Long id,
        String name,
        String summary,
        String city,
        String relocation,
        String englishFluency,
        LocalDateTime updatedAt,
        AuthorDTO author) {
}
