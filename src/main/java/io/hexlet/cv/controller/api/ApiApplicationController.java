package io.hexlet.cv.controller.api;

import io.hexlet.cv.dto.application.ApplicationDTOs.Details;
import io.hexlet.cv.dto.application.ApplicationDTOs.ListResponse;
import io.hexlet.cv.dto.application.ApplicationFormDTO;
import io.hexlet.cv.dto.application.EventFormDTO;
import io.hexlet.cv.dto.application.StageChangeDTO;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.model.enums.ApplicationStage;
import io.hexlet.cv.service.JobApplicationService;
import io.hexlet.cv.util.UserUtils;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Трекер откликов текущего пользователя (#1155).
 */
@RestController
@RequestMapping("/api/account/applications")
@AllArgsConstructor
public class ApiApplicationController {

    private final JobApplicationService service;
    private final UserUtils userUtils;

    @GetMapping
    public ListResponse index(@RequestParam(required = false) ApplicationStage stage,
                              @RequestParam(required = false) Long resumeId,
                              @RequestParam(defaultValue = "") String q) {
        return service.list(userUtils.getCurrentUser(), stage, resumeId, q);
    }

    @GetMapping("/{id}")
    public Details show(@PathVariable Long id) {
        return service.get(userUtils.getCurrentUser(), id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Details create(@Valid @RequestBody ApplicationFormDTO form) {
        return service.create(userUtils.getCurrentUser(), form);
    }

    @PutMapping("/{id}")
    public Details update(@PathVariable Long id, @Valid @RequestBody ApplicationFormDTO form) {
        return service.update(userUtils.getCurrentUser(), id, form);
    }

    @PatchMapping("/{id}/stage")
    public Details changeStage(@PathVariable Long id, @Valid @RequestBody StageChangeDTO dto) {
        return service.changeStage(userUtils.getCurrentUser(), id, dto);
    }

    @PostMapping("/{id}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public Details addEvent(@PathVariable Long id, @Valid @RequestBody EventFormDTO dto) {
        return service.addEvent(userUtils.getCurrentUser(), id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(userUtils.getCurrentUser(), id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<Map<String, Map<String, String>>> clientError(ClientException e) {
        return ResponseEntity.status(e.getStatus()).body(Map.of("errors", Map.of(e.getField(), e.getMessage())));
    }
}
