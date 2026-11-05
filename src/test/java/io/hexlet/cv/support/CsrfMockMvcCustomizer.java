package io.hexlet.cv.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcBuilderCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.setup.ConfigurableMockMvcBuilder;

/**
 * Подставляет валидный CSRF-токен во все запросы автоконфигурированного MockMvc,
 * как это делает фронт. Проверки без токена строят MockMvc вручную.
 */
@Component
public class CsrfMockMvcCustomizer implements MockMvcBuilderCustomizer {

    @Override
    public void customize(ConfigurableMockMvcBuilder<?> builder) {
        builder.defaultRequest(get("/").with(csrf()));
    }
}
