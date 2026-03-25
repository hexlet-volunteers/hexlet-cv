package io.hexlet.cv.validator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import io.hexlet.cv.handler.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommonPasswordValidator {

    private final Set<String> commonPasswords;
    private final Set<String> disposableDomains;

    public CommonPasswordValidator() {
        this.commonPasswords = loadFromClasspath("blacklists/10k-most-common.txt");
        this.disposableDomains = loadFromClasspath("blacklists/disposable_email_blocklist.conf");

        log.info("✅ Loaded {} common passwords and {} disposable domains",
                commonPasswords.size(), disposableDomains.size());
    }

    public boolean isCommonPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }
        return commonPasswords.contains(password.toLowerCase());
    }

    public boolean isDisposableEmail(String email) {
        if (email == null || !email.contains("@")) {
            return false;
        }
        String domain = extractDomain(email);
        return disposableDomains.contains(domain.toLowerCase());
    }

    private String extractDomain(String email) {
        return email.substring(email.lastIndexOf('@') + 1);
    }

    private Set<String> loadFromClasspath(String filePath) {

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                log.error("File not found: {}", filePath);
                throw new ServerException("Missing: " + filePath, "CONFIG_FILE_MISSING");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                Set<String> items = reader.lines()
                        .map(String::trim)
                        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());

                return Collections.unmodifiableSet(items);
            }
        } catch (IOException e) {
            log.error("Error reading: {}", filePath, e);
            throw new ServerException("Load failed: " + filePath, "CONFIG_LOAD_FAILED", e);
        }
    }
}
