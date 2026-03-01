package io.hexlet.cv.dto.reset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationDTO {

    private boolean isValid;
    private String email;
    private Long remainingMinutes;
}
