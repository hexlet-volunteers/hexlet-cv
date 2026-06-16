package io.hexlet.cv.dto.marketing;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DisplayOrderRequest {
    @NotNull(message = "display_order must not be null")
    @Min(value = 0, message = "display_order must be >= 0")
    @JsonProperty("display_order")
    private Integer displayOrder;
}
