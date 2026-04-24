package io.hexlet.cv.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

    // Генерация UUID токена (для email сброса пароля)
    public String generateUuidToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
