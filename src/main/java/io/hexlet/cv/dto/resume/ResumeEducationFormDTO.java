package io.hexlet.cv.dto.resume;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * Образование в форме резюме.
 */
@Getter
@Setter
public class ResumeEducationFormDTO {

    @NotBlank(message = "Укажите учебное заведение")
    @Size(max = 255)
    private String institution;

    @Size(max = 255)
    private String faculty;

    private LocalDate beginDate;

    private LocalDate endDate;

    private Boolean current;

    @Size(max = 255, message = "Не длиннее 255 символов")
    private String description;
}
