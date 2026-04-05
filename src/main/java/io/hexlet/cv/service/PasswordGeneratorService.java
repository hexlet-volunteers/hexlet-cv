package io.hexlet.cv.service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import io.hexlet.cv.handler.exception.ClientException;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.passay.PasswordValidator;
import org.passay.RuleResult;
import org.passay.WhitespaceRule;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PasswordGeneratorService {

    private static final int DEFAULT_PASSWORD_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 64;

    private final PasswordGenerator passwordGenerator = new PasswordGenerator();
    private final PasswordValidator passwordValidator;

    public PasswordGeneratorService() {

        this.passwordValidator = new PasswordValidator(
                new LengthRule(MIN_PASSWORD_LENGTH, MAX_PASSWORD_LENGTH),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new WhitespaceRule()
        );
    }

    public String generateStrongPassword(int length) {
        validateLength(length);

        CharacterRule upperCaseRule = new CharacterRule(EnglishCharacterData.UpperCase, 1);
        CharacterRule lowerCaseRule = new CharacterRule(EnglishCharacterData.LowerCase, 1);
        CharacterRule digitRule = new CharacterRule(EnglishCharacterData.Digit, 1);
        CharacterRule specialRule = new CharacterRule(EnglishCharacterData.Special, 1);

        return passwordGenerator.generatePassword(
                length,
                upperCaseRule,
                lowerCaseRule,
                digitRule,
                specialRule
        );
    }

    public String generateStrongPassword() {
        return generateStrongPassword(DEFAULT_PASSWORD_LENGTH);
    }

    public String generateMemorablePassword(int wordCount) {
        if (wordCount < 2) {
            wordCount = 2;
        }
        if (wordCount > 6) {
            wordCount = 6;
        }

        String[] dictionary = {
            "apple", "banana", "carrot", "dragon", "elephant", "flower",
            "guitar", "happiness", "island", "jupiter", "kangaroo", "lighthouse",
            "mountain", "notebook", "ocean", "penguin", "quantum", "rainbow",
            "sunshine", "tiger", "universe", "victory", "waterfall"
        };

        SecureRandom random = new SecureRandom();
        List<String> words = new ArrayList<>();

        for (int i = 0; i < wordCount; i++) {
            words.add(dictionary[random.nextInt(dictionary.length)]);
        }

        int number = random.nextInt(100);
        return String.join("-", words) + "-" + number;
    }

    public boolean isPasswordStrong(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        RuleResult result = passwordValidator.validate(new PasswordData(password));
        return result.isValid();
    }

    public List<String> getPasswordErrors(String password) {
        if (password == null) {
            return List.of("Password cannot be null");
        }
        RuleResult result = passwordValidator.validate(new PasswordData(password));
        if (result.isValid()) {
            return List.of();
        }
        return passwordValidator.getMessages(result);
    }

    private void validateLength(int length) {
        if (length < MIN_PASSWORD_LENGTH) {
            throw new ClientException("password",
                    "Password length must be at least " + MIN_PASSWORD_LENGTH,
                    HttpStatus.BAD_REQUEST);
        }
        if (length > MAX_PASSWORD_LENGTH) {
            throw new ClientException("password",
                    "Password length must not exceed " + MAX_PASSWORD_LENGTH,
                    HttpStatus.BAD_REQUEST);
        }
    }
}
