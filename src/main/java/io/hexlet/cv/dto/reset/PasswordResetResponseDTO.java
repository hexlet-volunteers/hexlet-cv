package io.hexlet.cv.dto.reset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponseDTO {

    private String email;
    private String message;
    private LocalDateTime resetAt;
}
