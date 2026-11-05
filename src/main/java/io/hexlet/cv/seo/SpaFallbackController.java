package io.hexlet.cv.seo;

import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Отдаёт собранный фронт на маршрутах SPA, у которых нет своей серверной страницы:
 * браузер открывает {@code /app/resumes} или {@code /login} напрямую, а не переходом внутри SPA.
 * Данные эти страницы берут из API, где и проверяется доступ, поэтому оболочка публичная.
 */
@Controller
public class SpaFallbackController {

    private static final MediaType HTML = new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8);

    private final SpaShell shell;

    public SpaFallbackController(SpaShell shell) {
        this.shell = shell;
    }

    @GetMapping(value = {"/login", "/register", "/password/forgot", "/password/reset",
        "/app", "/app/**"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> privatePages() {
        return ResponseEntity.ok().contentType(HTML).body(shell.plain(true));
    }

    @GetMapping(value = {"/blog", "/blog/{id}", "/legal"}, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> publicPages() {
        return ResponseEntity.ok().contentType(HTML).body(shell.plain(false));
    }
}
