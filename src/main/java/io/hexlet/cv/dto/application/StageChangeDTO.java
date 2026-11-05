package io.hexlet.cv.dto.application;

import io.hexlet.cv.model.enums.ApplicationStage;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Смена стадии — в том числе перетаскиванием на доске.
 */
@Getter
@Setter
public class StageChangeDTO {

    @NotNull(message = "Укажите этап")
    private ApplicationStage stage;

    /** Для отказа: на каком этапе получен. По умолчанию — текущий этап отклика. */
    private ApplicationStage rejectedAtStage;
}
