package io.hexlet.cv.validator;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class CommonPasswordValidator {

    private final Set<String> commonPasswords;
    private final Set<String> disposableDomains;

    public CommonPasswordValidator() {
        this.commonPasswords = loadCommonPasswordsFromFile();
        this.disposableDomains = loadDisposableDomainsFromFile();

        System.out.println("✅ Loaded " + commonPasswords.size() + " common passwords from file");
        System.out.println("✅ Loaded " + disposableDomains.size() + " disposable domains from file");
    }

    public boolean isCommonPassword(String password) {
        if (password == null) {
            return false;
        }
        return commonPasswords.contains(password.toLowerCase());
    }

    public boolean isDisposableEmail(String email) {
        if (email == null) {
            return false;
        }
        String domain = extractDomain(email);
        return disposableDomains.contains(domain.toLowerCase());
    }

    private String extractDomain(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            return email.substring(atIndex + 1);
        } else {
            return "";
        }
    }

    private Set<String> loadCommonPasswordsFromFile() {
        return loadFromClasspath("blacklists/10k-most-common.txt");
    }

    private Set<String> loadDisposableDomainsFromFile() {
        return loadFromClasspath("blacklists/disposable_email_blocklist.conf");
    }

    private Set<String> loadFromClasspath(String filePath) {
        Set<String> items = new HashSet<>();

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
            if (inputStream == null) {
                System.err.println("⚠️ File not found in classpath: " + filePath);
                return getFallbackData(filePath);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty() && !line.startsWith("#")) {
                        items.add(line.toLowerCase());
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("⚠️ Error reading file: " + filePath + " - " + e.getMessage());
            return getFallbackData(filePath);
        }

        return Collections.unmodifiableSet(items);
    }

    private Set<String> getFallbackData(String filePath) {
        if (filePath.contains("10k-most-common")) {
            // Fallback для паролей
            return Set.of(
                    "123456", "password", "12345678", "qwerty", "123456789",
                    "12345", "1234", "111111", "1234567", "dragon"
            );
        } else {
            // Fallback для disposable domains
            return Set.of(
                    "tempmail.com", "throwaway.com", "fakeinbox.com"
            );
        }
    }
}
