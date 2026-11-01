package io.hexlet.cv.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.regex.Pattern;

public class EmailDomainViaDnsApiValidator implements ConstraintValidator<EmailDomainViaDnsApi, String> {

    private static final Pattern EMAIL_REGEX = Pattern.compile("^[^@]+@([^@]+)$");
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || !EMAIL_REGEX.matcher(email).matches()) {
            return true; // @Email аннотация проверяет корректность
        }

        String domain = email.substring(email.indexOf('@') + 1);

        try {
            String url = "https://cloudflare-dns.com/dns-query?name=" + domain + "&type=MX";

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                    .header("Accept", "application/dns-json")
                    .GET().build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode root = OBJECT_MAPPER.readTree(response.body());

            JsonNode answer = root.get("Answer");
            return answer != null && answer.isArray() && answer.size() > 0;

        } catch (Exception e) {
            // Сеть недоступна, DNS API не ответил — считаем, что валидно
            return true;
        }
    }
}
