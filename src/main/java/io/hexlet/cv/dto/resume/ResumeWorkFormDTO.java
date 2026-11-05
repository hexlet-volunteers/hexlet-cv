package io.hexlet.cv.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * Место работы в форме резюме. Длина описания ограничена колонкой varchar(255) в resume_works.
 */
@Getter
@Setter
public class ResumeWorkFormDTO {

    @NotBlank(message = "Укажите компанию")
    @Size(max = 255)
    private String company;

    @Size(max = 255)
    private String position;

    private LocalDate beginDate;

    private LocalDate endDate;

    private Boolean current;

    @Size(max = 255, message = "Не длиннее 255 символов")
    private String description;
}
