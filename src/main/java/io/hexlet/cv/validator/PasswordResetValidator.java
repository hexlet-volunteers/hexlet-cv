package io.hexlet.cv.validator;

import io.hexlet.cv.dto.reset.PasswordResetDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.service.PasswordValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordResetValidator {

    private final PasswordValidationService passwordValidationService;
    private final MessageSource messageSource;

    public void validate(PasswordResetDTO dto, String firstName, String lastName) {

        if (dto.isAutoGenerate()) {
            return;
        }

        validatePasswordsMatch(dto.getPassword(), dto.getConfirmPassword());

        passwordValidationService.validatePassword(
                dto.getPassword(),
                firstName,
                lastName);
    }

    private void validatePasswordsMatch(String password, String confirmPassword) {
        if (password == null || !password.equals(confirmPassword)) {
            throw new ClientException("confirmPassword",
                    getMessage("password.mismatch"),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key, null,
                org.springframework.context.i18n.LocaleContextHolder.getLocale());
    }
}
