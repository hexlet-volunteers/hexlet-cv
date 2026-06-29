package io.hexlet.cv.dto.marketing;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class DisplayOrderRequestTest {

    private static Validator validator;
    private static ValidatorFactory factory;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    void shouldPassValidationWhenDisplayOrderIsZero() {
        DisplayOrderRequest request = createRequestWithDisplayOrder(0);

        Set<ConstraintViolation<DisplayOrderRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldPassValidationWhenDisplayOrderIsMaxValue() {
        DisplayOrderRequest request = createRequestWithDisplayOrder(Integer.MAX_VALUE);

        Set<ConstraintViolation<DisplayOrderRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void shouldFailValidationWhenDisplayOrderIsNull() {
        DisplayOrderRequest request = createRequestWithDisplayOrder(null);

        Set<ConstraintViolation<DisplayOrderRequest>> violations = validator.validate(request);

        assertThat(violations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("display_order must not be null");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("displayOrder");
                });
    }

    @Test
    void shouldFailValidationWhenDisplayOrderIsNegative() {
        DisplayOrderRequest request = createRequestWithDisplayOrder(-1);

        Set<ConstraintViolation<DisplayOrderRequest>> violations = validator.validate(request);

        assertThat(violations)
                .hasSize(1)
                .first()
                .satisfies(violation -> {
                    assertThat(violation.getMessage()).isEqualTo("display_order must be >= 0");
                    assertThat(violation.getPropertyPath().toString()).isEqualTo("displayOrder");
                });
    }

    private DisplayOrderRequest createRequestWithDisplayOrder(Integer value) {
        DisplayOrderRequest request = new DisplayOrderRequest();
        request.setDisplayOrder(value);
        return request;
    }
}
