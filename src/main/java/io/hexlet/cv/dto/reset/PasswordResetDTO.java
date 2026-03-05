package io.hexlet.cv.dto.reset;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {

//    @NotBlank(message = "Token cannot be blank")
//    private String token;

    private String password;
    private String confirmPassword;
    private boolean autoGenerate = false;

    @AssertTrue(message = "Passwords do not match")
    public boolean isPasswordsMatch() {
        if (autoGenerate) {
            return true;
        }
        return password != null && password.equals(confirmPassword);
    }

    @AssertTrue(message = "Password is required when autoGenerate is false")
    public boolean isPasswordPresent() {
        if (autoGenerate) {
            return true;
        }
        return password != null && !password.isBlank();
    }

    @AssertTrue(message = "Password must be at least 8 characters and contain uppercase,"
            + " lowercase, number and special character")
    public boolean isPasswordStrong() {
        if (autoGenerate) {
            return true;
        }
        if (password == null || password.isBlank()) {
            return false;
        }
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        return password.matches(passwordRegex);
    }
}
