package io.hexlet.cv.util;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordGeneratorService {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;


    private final SecureRandom random = new SecureRandom();

    public String generateStrongPassword() {
        return generatePassword(20);
    }

    public String generatePassword(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        StringBuilder password = new StringBuilder();

        password.append(UPPER.charAt(random.nextInt(UPPER.length())));
        password.append(LOWER.charAt(random.nextInt(LOWER.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        for (int i = 4; i < length; i++) {
            password.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        return shuffleString(password.toString());
    }

    public String generateMemorablePassword(int wordCount) {

        String[] words = {"apple", "banana", "carrot", "dragon", "elephant", "flower",
                "guitar", "happiness", "island", "jupiter", "kangaroo", "lighthouse",
                "mountain", "notebook", "ocean", "penguin", "quantum", "rainbow",
                "sunshine", "tiger", "universe", "victory", "waterfall", "xylophone"};

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < wordCount; i++) {
            if (i > 0) {
                password.append("-");
            }
            password.append(words[random.nextInt(words.length)]);
        }

        return password.toString();
    }

    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars()
                .anyMatch(ch -> SPECIAL.indexOf(ch) >= 0);

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public int passwordStrength(String password) {
        if (password == null || password.length() < 8) return 1;

        int score = 0;

        if (password.length() >= 12) score++;

        if (password.chars().anyMatch(Character::isUpperCase)) score++;
        if (password.chars().anyMatch(Character::isLowerCase)) score++;
        if (password.chars().anyMatch(Character::isDigit)) score++;
        if (password.chars().anyMatch(ch -> SPECIAL.indexOf(ch) >= 0)) score++;

        return Math.min(score, 5);
    }


    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            int j = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
