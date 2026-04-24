package io.hexlet.cv.dto.reset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {

    private String password;
    private String confirmPassword;
    private boolean autoGenerate = false;
}
