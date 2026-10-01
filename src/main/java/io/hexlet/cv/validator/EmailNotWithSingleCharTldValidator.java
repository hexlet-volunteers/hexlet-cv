package io.hexlet.cv.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailNotWithSingleCharTldValidator implements ConstraintValidator<EmailNotWithSingleCharTld, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || !email.contains(".")) {
            return true;
        }

        int lastDotIndex = email.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == email.length() - 1) {
            return true;
        }

        String tld = email.substring(lastDotIndex + 1);
        return tld.length() >= 2;
    }
}
