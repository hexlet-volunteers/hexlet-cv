package io.hexlet.cv.dto.user.auth;

import io.hexlet.cv.validator.EmailDomainViaDnsApi;
import io.hexlet.cv.validator.EmailNotWithSingleCharTld;
import io.hexlet.cv.validator.NotInDisposableEmailDomains;
import io.hexlet.cv.validator.NotInTop10K;
import io.hexlet.cv.validator.PasswordNotSimilarToUser;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@PasswordNotSimilarToUser
public class RegistrationRequestDTO {
    @NotBlank(message = "{email.notBlank}")
    @Email(message = "{email.invalid}")
    @EmailNotWithSingleCharTld
    @NotInDisposableEmailDomains
    @EmailDomainViaDnsApi
    @Size(max = 255, message = "{email.maxSize}")
    private String email;

    @NotBlank(message = "{password.notBlank}")
    @Size(min = 8, max = 255, message = "{password.size}")
    @NotInTop10K
    private String password;

    @NotBlank(message = "{user.firstName.notBlank}")
    @Size(max = 255, message = "{user.firstName.maxSize}")
    private String firstName;

    @NotBlank(message = "{user.lastName.notBlank}")
    @Size(max = 255, message = "{user.lastName.maxSize}")
    private String lastName;

    @NotNull(message = "{user.terms.required}")
    @AssertTrue(message = "{user.terms.accepted}")
    private Boolean terms;
}
