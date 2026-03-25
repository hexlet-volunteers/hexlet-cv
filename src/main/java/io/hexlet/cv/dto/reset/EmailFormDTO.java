package io.hexlet.cv.dto.reset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailFormDTO {

    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.invalid}")
    private String email;
}
