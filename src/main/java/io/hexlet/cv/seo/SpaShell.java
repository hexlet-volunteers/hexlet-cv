package io.hexlet.cv.seo;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

/**
 * HTML-оболочка SPA с метаданными и готовым содержимым для поисковиков.
 *
 * <p>В проде берётся собранный {@code static/index.html} (его кладёт туда Dockerfile), поэтому
 * скрипты и стили фронта подключаются как обычно, а React после загрузки заменяет
 * содержимое {@code #root} интерфейсом. Без сборки (dev, тесты) — минимальная оболочка.
 */
@Component
public class SpaShell {

    private static final String FALLBACK = """
            <!doctype html>
            <html lang="ru">
              <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Хекслет Карьера</title>
              </head>
              <body>
                <div id="root"></div>
              </body>
            </html>
            """;

    private final String template;

    public SpaShell() {
        this.template = load();
    }

    /**
     * Метаданные страницы.
     *
     * @param title заголовок
     * @param description описание для сниппета
     * @param canonical абсолютный канонический адрес
     * @param noindex запретить индексацию (страница «не найдено»)
     */
    public record Meta(String title, String description, String canonical, boolean noindex) {
    }

    /**
     * Собирает страницу.
     *
     * @param meta заголовок и метатеги
     * @param bodyHtml готовая разметка содержимого, уже экранированная
     * @return HTML-документ
     */
    public String render(Meta meta, String bodyHtml) {
        var title = escape(meta.title());
        var description = escape(meta.description());
        var head = new StringBuilder()
                .append("<title>").append(title).append("</title>\n")
                .append("    <meta name=\"description\" content=\"").append(description).append("\" />\n")
                .append("    <meta property=\"og:title\" content=\"").append(title).append("\" />\n")
                .append("    <meta property=\"og:description\" content=\"").append(description).append("\" />\n")
                .append("    <meta property=\"og:type\" content=\"website\" />\n")
                .append("    <meta property=\"og:site_name\" content=\"Хекслет Карьера\" />\n");
        if (meta.canonical() != null) {
            var url = escape(meta.canonical());
            head.append("    <link rel=\"canonical\" href=\"").append(url).append("\" />\n")
                    .append("    <meta property=\"og:url\" content=\"").append(url).append("\" />\n");
        }
        if (meta.noindex()) {
            head.append("    <meta name=\"robots\" content=\"noindex\" />\n");
        }
        return template
                .replaceFirst("(?s)<title>.*?</title>", Matcher.quoteReplacement(head.toString()))
                .replace("<div id=\"root\"></div>", "<div id=\"root\">" + bodyHtml + "</div>");
    }

    public static String escape(String value) {
        return value == null ? "" : HtmlUtils.htmlEscape(value, StandardCharsets.UTF_8.name());
    }

    private static String load() {
        var resource = new ClassPathResource("static/index.html");
        if (!resource.exists()) {
            return FALLBACK;
        }
        try (var in = resource.getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
