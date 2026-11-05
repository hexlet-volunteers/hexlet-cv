package io.hexlet.cv.dto.application;

import io.hexlet.cv.model.enums.ApplicationStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Создание и правка отклика. Стадия при правке не меняется — для этого отдельная операция.
 */
@Getter
@Setter
public class ApplicationFormDTO {

    @NotBlank(message = "Укажите компанию")
    @Size(max = 255)
    private String company;

    @NotBlank(message = "Укажите позицию")
    @Size(max = 255)
    private String position;

    @Size(max = 255)
    private String salary;

    @Size(max = 1000)
    @Pattern(regexp = "^$|^https?://.+", message = "Ссылка должна начинаться с http:// или https://")
    private String vacancyUrl;

    @Size(max = 255)
    private String source;

    /** Стадия при создании; по умолчанию «Сохранено». */
    private ApplicationStage stage;

    private Long resumeId;

    private LocalDate deadline;

    @Size(max = 255)
    private String nextStepTitle;

    private LocalDateTime nextStepAt;
}
