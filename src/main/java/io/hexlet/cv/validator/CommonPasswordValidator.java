package io.hexlet.cv.validator;

import io.hexlet.cv.dto.user.auth.RegistrationRequestDTO;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommonPasswordValidator {

    private final Validator validator;

    public boolean isCommonPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }

        var violations = validator.validateValue(RegistrationRequestDTO.class, "password", password);

        return violations.stream()
                .anyMatch(v -> v.getConstraintDescriptor().getAnnotation() instanceof NotInTop10K);
    }

    public boolean isDisposableEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }

        var violations = validator.validateValue(RegistrationRequestDTO.class, "email", email);

        return violations.stream()
                .anyMatch(v -> v.getConstraintDescriptor().getAnnotation() instanceof NotInDisposableEmailDomains);
    }
}
