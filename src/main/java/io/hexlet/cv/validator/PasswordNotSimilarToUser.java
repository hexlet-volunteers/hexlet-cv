package io.hexlet.cv.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = PasswordNotSimilarToUserValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordNotSimilarToUser {
    Class<?>[] groups() default {};
    String message() default "{password.notSimilar}";
    Class<? extends Payload>[] payload() default {};
}
