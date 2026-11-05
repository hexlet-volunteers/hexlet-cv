package io.hexlet.cv.dto.user.auth;

import io.hexlet.cv.validator.NotInTop10K;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Ссылка недействительна")
    @Size(max = 200)
    private String token;

    @NotBlank(message = "{password.notBlank}")
    @Size(min = 8, max = 255, message = "{password.size}")
    @NotInTop10K
    private String password;
}
