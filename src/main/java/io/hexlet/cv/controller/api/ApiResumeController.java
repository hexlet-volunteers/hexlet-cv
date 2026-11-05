package io.hexlet.cv.controller.api;

import io.hexlet.cv.dto.api.PageDTO;
import io.hexlet.cv.dto.resume.PublicProfileDTO;
import io.hexlet.cv.dto.resume.ResumeDetailsDTO;
import io.hexlet.cv.dto.resume.ResumeSummaryDTO;
import io.hexlet.cv.service.ResumeApiService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Публичные резюме: каталог с поиском, страница резюме и профиль автора.
 * Доступны без входа — заменяют страницы старого сайта (#1310).
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiResumeController {

    private final ResumeApiService resumeApiService;

    @GetMapping("/resumes")
    public PageDTO<ResumeSummaryDTO> index(@RequestParam(defaultValue = "") String q,
                                           @RequestParam(defaultValue = "1") int page) {
        return resumeApiService.search(q, page);
    }

    @GetMapping("/resumes/{id}")
    public ResumeDetailsDTO show(@PathVariable Long id) {
        return resumeApiService.getPublished(id);
    }

    @GetMapping("/users/{id}")
    public PublicProfileDTO profile(@PathVariable Long id) {
        return resumeApiService.getProfile(id);
    }
}
