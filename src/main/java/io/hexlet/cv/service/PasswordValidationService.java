package io.hexlet.cv.service;

import io.hexlet.cv.dto.user.auth.RegistrationRequestDTO;
import io.hexlet.cv.handler.exception.ClientException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PasswordValidationService {

    private final Validator validator;

    public void validatePassword(String password, String firstName, String lastName) {

        RegistrationRequestDTO dummyDto = new RegistrationRequestDTO();
        dummyDto.setPassword(password);
        dummyDto.setFirstName(firstName);
        dummyDto.setLastName(lastName);
        dummyDto.setEmail("dummy@example.com");

        var violations = validator.validate(dummyDto);

        if (!violations.isEmpty()) {
            var violation = violations.stream()
                    .filter(v -> v.getPropertyPath().toString().equals("password")
                            || v.getPropertyPath().toString().isEmpty())
                    .findFirst()
                    .orElse(violations.iterator().next());

            throw new ClientException("password", violation.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void validateEmail(String email) {

        var violations = validator.validateValue(RegistrationRequestDTO.class, "email", email);
        if (!violations.isEmpty()) {
            throw new ClientException("email", violations.iterator().next().getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
