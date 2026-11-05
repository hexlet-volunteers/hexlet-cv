package io.hexlet.cv.dto.application;

import io.hexlet.cv.model.enums.ApplicationEventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * Событие, которое пользователь добавляет в таймлайн вручную.
 */
@Getter
@Setter
public class EventFormDTO {

    @NotNull(message = "Укажите тип события")
    private ApplicationEventType type;

    @NotBlank(message = "Опишите событие")
    @Size(max = 1000)
    private String text;

    /** Когда произошло; по умолчанию — сейчас. */
    private LocalDateTime occurredAt;
}
