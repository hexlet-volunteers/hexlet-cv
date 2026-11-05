package io.hexlet.cv.dto.resume;

import java.time.LocalDate;

// Образование в резюме.
public record ResumeEducationDTO(
        String institution,
        String faculty,
        LocalDate beginDate,
        LocalDate endDate,
        Boolean current,
        String description) {
}
