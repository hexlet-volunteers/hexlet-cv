package io.hexlet.cv.controller.api;

import io.hexlet.cv.dto.resume.OwnResumeDTO;
import io.hexlet.cv.dto.resume.ResumeFormDTO;
import io.hexlet.cv.service.ResumeApiService;
import io.hexlet.cv.util.UserUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Резюме текущего пользователя в кабинете: список, создание и редактирование (#1309).
 */
@RestController
@RequestMapping("/api/account/resumes")
@AllArgsConstructor
public class ApiAccountResumeController {

    private final ResumeApiService resumeApiService;
    private final UserUtils userUtils;

    @GetMapping
    public List<OwnResumeDTO> index() {
        return resumeApiService.listOwn(userUtils.getCurrentUser());
    }

    @GetMapping("/{id}")
    public OwnResumeDTO show(@PathVariable Long id) {
        return resumeApiService.getOwn(userUtils.getCurrentUser(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OwnResumeDTO create(@Valid @RequestBody ResumeFormDTO form) {
        return resumeApiService.create(userUtils.getCurrentUser(), form);
    }

    @PutMapping("/{id}")
    public OwnResumeDTO update(@PathVariable Long id, @Valid @RequestBody ResumeFormDTO form) {
        return resumeApiService.update(userUtils.getCurrentUser(), id, form);
    }
}
