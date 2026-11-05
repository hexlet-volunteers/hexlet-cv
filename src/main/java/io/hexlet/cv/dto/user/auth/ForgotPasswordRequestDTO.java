package io.hexlet.cv.dto.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.invalid}")
    @Size(max = 255, message = "{email.maxSize}")
    private String email;
}
