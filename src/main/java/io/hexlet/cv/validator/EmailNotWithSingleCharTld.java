package io.hexlet.cv.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EmailNotWithSingleCharTldValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailNotWithSingleCharTld {
    Class<?>[] groups() default {};
    String message() default "{email.tld.minSize}";

    Class<? extends Payload>[] payload() default {};
}
