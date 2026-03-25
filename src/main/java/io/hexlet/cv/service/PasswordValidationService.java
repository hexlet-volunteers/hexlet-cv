package io.hexlet.cv.service;

import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.validator.CommonPasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordValidationService {

    private final CommonPasswordValidator commonPasswordValidator;
    private final MessageSource messageSource;

    private static final int MIN_LENGTH = 8;
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String DIGIT_PATTERN = ".*\\d.*";

    public void validatePassword(String password) {

        if (password == null) {
            throw new ClientException("password", "Password cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (password.length() < MIN_LENGTH) {
            throw new ClientException("password",
                    "Password must be at least " + MIN_LENGTH + " characters long",
                    HttpStatus.BAD_REQUEST);
        }

        if (!password.matches(UPPERCASE_PATTERN)) {
            throw new ClientException("password",
                    "Password must contain at least one uppercase letter",
                    HttpStatus.BAD_REQUEST);
        }

        if (!password.matches(LOWERCASE_PATTERN)) {
            throw new ClientException("password",
                    "Password must contain at least one lowercase letter",
                    HttpStatus.BAD_REQUEST);
        }

        if (!password.matches(DIGIT_PATTERN)) {
            throw new ClientException("password",
                    "Password must contain at least one digit",
                    HttpStatus.BAD_REQUEST);
        }

        if (commonPasswordValidator.isCommonPassword(password)) {
            throw new ClientException("password", getMsg("password.common"), HttpStatus.BAD_REQUEST);
        }
    }

    private String getMsg(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    public void validateEmail(String email) {
        if (commonPasswordValidator.isDisposableEmail(email)) {
            throw new ClientException("email",
                    "Disposable email addresses are not allowed",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
