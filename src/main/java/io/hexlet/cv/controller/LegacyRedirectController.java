package io.hexlet.cv.controller;

import io.hexlet.cv.repository.ResumeRepository;
import io.hexlet.cv.repository.UserRepository;
import io.hexlet.cv.service.ResumeApiService;
import java.net.URI;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 301-редиректы со старых адресов Rails-приложения ({@code /ru/...}, {@code /en/...}) на новые (#1312).
 *
 * <p>На старые страницы ведут внешние ссылки и поисковики. Резюме и профили, которых больше нет,
 * отдают 404 сразу, без редиректа: так поисковик быстрее уберёт их из выдачи.
 */
@Controller
@AllArgsConstructor
public class LegacyRedirectController {

    private static final String LOCALE = "{locale:ru|en}";

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    /**
     * Старая главная — каталог резюме с поиском. Поисковый запрос в старом приложении
     * приходил как {@code q} или как ransack-фильтр {@code q[..._cont]}.
     *
     * @param params параметры запроса старого адреса
     * @return редирект на каталог с тем же запросом и страницей
     */
    @GetMapping({"/" + LOCALE, "/" + LOCALE + "/", "/" + LOCALE + "/resumes"})
    public ResponseEntity<Void> catalog(@RequestParam Map<String, String> params) {
        var target = UriComponentsBuilder.fromPath("/resumes");
        var query = params.get("q");
        if (query == null) {
            query = params.entrySet().stream()
                    .filter(e -> e.getKey().startsWith("q[") && e.getKey().endsWith("_cont]"))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);
        }
        if (query != null && !query.isBlank()) {
            target.queryParam("q", query.trim());
        }
        var page = params.get("page");
        if (page != null && page.matches("\\d{1,6}") && !"1".equals(page)) {
            target.queryParam("page", page);
        }
        return moved(target.encode().build().toUriString());
    }

    @GetMapping("/" + LOCALE + "/resumes/{id:\\d+}")
    public ResponseEntity<Void> resume(@PathVariable Long id) {
        return resumeRepository.findByIdAndState(id, ResumeApiService.PUBLISHED).isPresent()
                ? moved("/resumes/" + id)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/" + LOCALE + "/users/{id:\\d+}")
    public ResponseEntity<Void> user(@PathVariable Long id) {
        return userRepository.existsById(id)
                ? moved("/profiles/" + id)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/" + LOCALE + "/users/sign_in")
    public ResponseEntity<Void> signIn() {
        return moved("/login");
    }

    @GetMapping("/" + LOCALE + "/users/sign_up")
    public ResponseEntity<Void> signUp() {
        return moved("/register");
    }

    @GetMapping("/" + LOCALE + "/users/password/new")
    public ResponseEntity<Void> forgotPassword() {
        return moved("/password/forgot");
    }

    @GetMapping("/" + LOCALE + "/vacancies")
    public ResponseEntity<Void> vacancies() {
        return moved("/");
    }

    private static ResponseEntity<Void> moved(String location) {
        var headers = new HttpHeaders();
        headers.setLocation(URI.create(location));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }
}
