package io.hexlet.cv.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Component
public class TokenGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    // Генерация UUID токена (для email сброса пароля)
    public String generateUuidToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // Генерация cryptographically secure токена
    public String generateSecureToken(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // Генерация numeric токена (для SMS кодов)
    public String generateNumericToken(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    // Генерация hex токена
    public String generateHexToken(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Генерация токена с префиксом
    public String generatePrefixedToken(String prefix, int length) {
        return prefix + "_" + generateSecureToken(length);
    }
}
