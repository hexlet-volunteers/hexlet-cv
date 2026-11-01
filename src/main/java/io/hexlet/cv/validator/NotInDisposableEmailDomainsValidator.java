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
public class NotInDisposableEmailDomainsValidator implements ConstraintValidator<NotInDisposableEmailDomains, String> {

    private final Set<String> disposableEmailBlocklist = new HashSet<>();

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || !email.contains("@")) {
            return true;
        }

        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();
        return !disposableEmailBlocklist.contains(domain);
    }

    @PostConstruct
    public void loadDisposableEmailBlocklist() {
        try (var inputStream = new ClassPathResource("blacklists/disposable_email_blocklist.conf").getInputStream();
                var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim().toLowerCase();
                if (!line.isEmpty()) {
                    disposableEmailBlocklist.add(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить список одноразовых доменов email", e);
        }
    }
}
