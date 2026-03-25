package io.hexlet.cv.dto.reset;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponseDTO {

    private String email;
    private String message;
    private LocalDateTime resetAt;
}
