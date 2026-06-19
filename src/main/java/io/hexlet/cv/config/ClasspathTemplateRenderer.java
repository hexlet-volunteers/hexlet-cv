package io.hexlet.cv.config;

import io.github.inertia4j.spi.TemplateRenderer;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClasspathTemplateRenderer implements TemplateRenderer {
    private static final String PAGE_OBJECT_PLACEHOLDER = "@PageObject@";

    private final String template;

    public ClasspathTemplateRenderer(String templatePath) {
        this.template = loadTemplate(templatePath);
    }

    @Override
    public String render(String pageObjectJson) {
        return template.replace(PAGE_OBJECT_PLACEHOLDER, escapeHtmlAttribute(pageObjectJson));
    }

    private String loadTemplate(String templatePath) {
        var classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                throw new IllegalStateException(
                    "Template file not found at classpath resource path: " + templatePath
                );
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException(
                "Failed to read template from classpath resource path: " + templatePath,
                e
            );
        }
    }

    private String escapeHtmlAttribute(String value) {
        return value
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }
}
