package io.hexlet.cv.dto.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDTO {

    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.invalid}")
    private String email;

    @NotBlank(message = "{password.notBlank}")
    @Size(min = 8, message = "{password.minSize}")
    private String password;


}
