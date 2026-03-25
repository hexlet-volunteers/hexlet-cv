package io.hexlet.cv.reset;


import static org.assertj.core.api.Assertions.assertThat;

import io.hexlet.cv.validator.CommonPasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommonPasswordValidatorTest {

    private CommonPasswordValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CommonPasswordValidator();
    }

    @Test
    void shouldDetectCommonPasswords() {
        assertThat(validator.isCommonPassword("123456")).isTrue();
        assertThat(validator.isCommonPassword("password")).isTrue();
    }

    @Test
    void shouldDetectDisposableEmails() {
        assertThat(validator.isDisposableEmail("test@0-mail.com")).isTrue();
    }

    @Test
    void shouldHandleCaseInsensitivity() {
        assertThat(validator.isCommonPassword("PASSWORD")).isTrue();
        assertThat(validator.isDisposableEmail("USER@0-MAIL.COM")).isTrue();
    }
}
