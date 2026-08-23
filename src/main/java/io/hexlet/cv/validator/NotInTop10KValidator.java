package io.hexlet.cv.validator;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class NotInTop10KValidator implements ConstraintValidator<NotInTop10K, String> {

    private final Set<String> blacklist = new HashSet<>();

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return password != null && !blacklist.contains(password.toLowerCase());
    }

    @PostConstruct
    public void loadBlacklist() {
        try (var inputStream = new ClassPathResource("blacklists/10k-most-common.txt").getInputStream();
                var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                blacklist.add(line.trim());
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить топ-10k паролей", e);
        }
    }
}
