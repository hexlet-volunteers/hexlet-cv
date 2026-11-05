package io.hexlet.cv.dto.resume;

import java.util.List;

// Публичный профиль пользователя с его опубликованными резюме.
public record PublicProfileDTO(Long id, String firstName, String lastName, String about,
                               List<ResumeSummaryDTO> resumes) {
}
