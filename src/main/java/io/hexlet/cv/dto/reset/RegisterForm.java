package io.hexlet.cv.dto.reset;

import io.hexlet.cv.dto.user.auth.RegistrationRequestDTO;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterForm extends RegistrationRequestDTO {

    @NotBlank(message = "{passwordConfirmation.notBlank}")
    private String passwordConfirmation;

    @AssertTrue(message = "{password.mismatch}")
    public boolean isPasswordsMatch() {
        return getPassword() != null && getPassword().equals(passwordConfirmation);
    }
}
