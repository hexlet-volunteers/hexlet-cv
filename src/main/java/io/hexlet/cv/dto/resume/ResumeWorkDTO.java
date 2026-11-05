package io.hexlet.cv.dto.resume;

import java.time.LocalDate;

// Место работы в резюме.
public record ResumeWorkDTO(
        String company,
        String position,
        LocalDate beginDate,
        LocalDate endDate,
        Boolean current,
        String description) {
}
