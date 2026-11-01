package io.hexlet.cv.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EmailDomainViaDnsApiValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailDomainViaDnsApi {
    Class<?>[] groups() default {};
    String message() default "{email.domain.not.exists}";
    Class<? extends Payload>[] payload() default {};
}
