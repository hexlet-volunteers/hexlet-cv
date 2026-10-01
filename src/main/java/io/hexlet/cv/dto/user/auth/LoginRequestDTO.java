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
    @Size(max = 255, message = "{email.maxSize}")
    private String email;

    @NotBlank(message = "{password.notBlank}")
    @Size(min = 8, max = 255, message = "{password.size}")
    private String password;


}
