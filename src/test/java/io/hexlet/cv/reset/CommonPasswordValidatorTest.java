package io.hexlet.cv.reset;


import io.hexlet.cv.validator.CommonPasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
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
    void shouldAllowStrongPasswords() {
        assertThat(validator.isCommonPassword("MySecurePass123!")).isFalse();
        assertThat(validator.isCommonPassword("Another$tr0ngP@ss")).isFalse();
    }

    @Test
    void shouldDetectDisposableEmails() {
        assertThat(validator.isDisposableEmail("test@0-mail.com")).isTrue();
    }

    @Test
    void shouldAllowRegularEmails() {
        assertThat(validator.isDisposableEmail("user@gmail.com")).isFalse();
    }

    @Test
    void shouldHandleNullInputs() {
        assertThat(validator.isCommonPassword(null)).isFalse();
        assertThat(validator.isDisposableEmail(null)).isFalse();
    }
}
