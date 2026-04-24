package io.hexlet.cv.dto.user.auth;

import io.hexlet.cv.validator.EmailDomainViaDnsApi;
import io.hexlet.cv.validator.EmailNotWithSingleCharTld;
import io.hexlet.cv.validator.NotInDisposableEmailDomains;
import io.hexlet.cv.validator.NotInTop10K;
import io.hexlet.cv.validator.PasswordNotSimilarToUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@PasswordNotSimilarToUser
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDTO {
    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.invalid}")
    @EmailNotWithSingleCharTld
    @NotInDisposableEmailDomains
    @EmailDomainViaDnsApi
    private String email;

    @NotBlank(message = "{password.notBlank}")
    @Size(min = 8, message = "{password.minSize}")
    @NotInTop10K
    private String password;

    @NotBlank(message = "{user.firstName.notBlank}")
    private String firstName;

    @NotBlank(message = "{user.lastName.notBlank}")
    private String lastName;
}
