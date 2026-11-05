package io.hexlet.cv.seo;

import io.hexlet.cv.dto.resume.PublicProfileDTO;
import io.hexlet.cv.dto.resume.ResumeDetailsDTO;
import io.hexlet.cv.dto.resume.ResumeSummaryDTO;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.service.ResumeApiService;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Публичные страницы, которые должны находиться поисковиками: каталог, резюме и профиль.
 *
 * <p>Отдаёт HTML с заголовком, описанием, каноническим адресом и текстом страницы в {@code #root}.
 * Пользователь получает тот же SPA: React после загрузки рисует интерфейс поверх. Несуществующее
 * резюме — честный 404 с {@code noindex}, а не пустая страница с кодом 200.
 */
@Controller
public class SeoPageController {

    private static final int DESCRIPTION_LIMIT = 160;

    private final ResumeApiService resumes;
    private final SpaShell shell;
    private final String publicUrl;

    public SeoPageController(ResumeApiService resumes, SpaShell shell,
                             @Value("${app.public-url:http://localhost:5173}") String publicUrl) {
        this.resumes = resumes;
        this.shell = shell;
        this.publicUrl = publicUrl.replaceAll("/+$", "");
    }

    @GetMapping(value = "/resumes", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> catalog(@RequestParam(defaultValue = "") String q,
                                          @RequestParam(defaultValue = "1") int page) {
        var result = resumes.search(q, page);
        var items = result.items().stream().map(SeoPageController::card).collect(Collectors.joining());
        var body = "<main><h1>Резюме программистов</h1>"
                + "<p>Опубликованные резюме студентов и выпускников Хекслета.</p>"
                + "<ul>" + items + "</ul></main>";
        // Поисковая выдача с запросом не индексируется — иначе дублей будет столько же, сколько запросов.
        var canonical = publicUrl + "/resumes" + (page > 1 && q.isBlank() ? "?page=" + page : "");
        var meta = new SpaShell.Meta("Резюме программистов — Хекслет Карьера",
                "Резюме начинающих разработчиков: студенты и выпускники Хекслета ищут работу в IT.",
                canonical, !q.isBlank());
        return html(HttpStatus.OK, shell.render(meta, body));
    }

    @GetMapping(value = "/resumes/{id:\\d+}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> resume(@PathVariable Long id) {
        ResumeDetailsDTO r;
        try {
            r = resumes.getPublished(id);
        } catch (ResourceNotFoundException e) {
            return notFound();
        }
        var author = name(r.author().firstName(), r.author().lastName());
        var body = new StringBuilder("<main><article>")
                .append("<h1>").append(SpaShell.escape(r.name())).append("</h1>")
                .append("<p><a href=\"/profiles/").append(r.author().id()).append("\">")
                .append(SpaShell.escape(author)).append("</a>");
        if (r.city() != null) {
            body.append(" · ").append(SpaShell.escape(r.city()));
        }
        body.append("</p>");
        section(body, "О себе", r.summary());
        section(body, "Навыки", r.skillsDescription());
        if (!r.works().isEmpty()) {
            body.append("<h2>Опыт работы</h2><ul>");
            r.works().forEach(w -> body.append("<li>")
                    .append(SpaShell.escape(join(" · ", w.position(), w.company()))).append("</li>"));
            body.append("</ul>");
        }
        section(body, "Проекты", r.projectsDescription());
        if (!r.educations().isEmpty()) {
            body.append("<h2>Образование</h2><ul>");
            r.educations().forEach(e -> body.append("<li>")
                    .append(SpaShell.escape(join(", ", e.institution(), e.faculty()))).append("</li>"));
            body.append("</ul>");
        }
        body.append("</article></main>");

        var description = firstNonBlank(r.summary(), r.skillsDescription(),
                "Резюме " + author + " на Хекслет Карьере");
        var meta = new SpaShell.Meta(r.name() + " — " + author + " | Хекслет Карьера",
                truncate(description), publicUrl + "/resumes/" + r.id(), false);
        return html(HttpStatus.OK, shell.render(meta, body.toString()));
    }

    @GetMapping(value = "/profiles/{id:\\d+}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> profile(@PathVariable Long id) {
        PublicProfileDTO p;
        try {
            p = resumes.getProfile(id);
        } catch (ResourceNotFoundException e) {
            return notFound();
        }
        var author = name(p.firstName(), p.lastName());
        var items = p.resumes().stream().map(SeoPageController::card).collect(Collectors.joining());
        var body = "<main><h1>" + SpaShell.escape(author) + "</h1>"
                + (p.about() == null ? "" : "<p>" + SpaShell.escape(p.about()) + "</p>")
                + "<h2>Резюме</h2><ul>" + items + "</ul></main>";
        // Профиль без опубликованных резюме поисковику не нужен.
        var meta = new SpaShell.Meta(author + " — Хекслет Карьера",
                truncate(firstNonBlank(p.about(), "Профиль " + author + " на Хекслет Карьере")),
                publicUrl + "/profiles/" + p.id(), p.resumes().isEmpty());
        return html(HttpStatus.OK, shell.render(meta, body));
    }

    private ResponseEntity<String> notFound() {
        var meta = new SpaShell.Meta("Страница не найдена — Хекслет Карьера", "", null, true);
        return html(HttpStatus.NOT_FOUND, shell.render(meta, "<main><h1>Страница не найдена</h1></main>"));
    }

    private static String card(ResumeSummaryDTO r) {
        return "<li><a href=\"/resumes/" + r.id() + "\">" + SpaShell.escape(r.name()) + "</a> — "
                + SpaShell.escape(name(r.author().firstName(), r.author().lastName()))
                + (r.city() == null ? "" : ", " + SpaShell.escape(r.city())) + "</li>";
    }

    private static void section(StringBuilder body, String title, String text) {
        if (text != null && !text.isBlank()) {
            body.append("<h2>").append(title).append("</h2><p>").append(SpaShell.escape(text)).append("</p>");
        }
    }

    private static String name(String first, String last) {
        var value = join(" ", first, last);
        return value.isBlank() ? "Без имени" : value;
    }

    private static String join(String separator, String... parts) {
        return Stream.of(parts).filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(separator));
    }

    private static String firstNonBlank(String... values) {
        return Stream.of(values).filter(v -> v != null && !v.isBlank()).findFirst().orElse("");
    }

    private static String truncate(String text) {
        var oneLine = text.replaceAll("\\s+", " ").trim();
        return oneLine.length() <= DESCRIPTION_LIMIT ? oneLine : oneLine.substring(0, DESCRIPTION_LIMIT - 1) + "…";
    }

    private static ResponseEntity<String> html(HttpStatus status, String body) {
        return ResponseEntity.status(status).contentType(new MediaType(MediaType.TEXT_HTML,
                StandardCharsets.UTF_8)).body(body);
    }
}
