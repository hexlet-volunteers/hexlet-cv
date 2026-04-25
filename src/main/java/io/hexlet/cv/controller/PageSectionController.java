package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.pagesection.PageSectionCreateDTO;
import io.hexlet.cv.dto.pagesection.PageSectionUpdateDTO;
import io.hexlet.cv.service.PageSectionService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/pages/sections")
@PreAuthorize("hasRole('ADMIN')")
public class PageSectionController {

    private final Inertia inertia;
    private final PageSectionService pageSectionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> getAll(
        @RequestParam(required = false) String page,
        @RequestParam(required = false) Boolean active) {

        var sections = pageSectionService.findAllOnPage(page, active);
        return inertia.render("sections/Index", Map.of("pageSections", sections));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> get(@PathVariable Long id) {

        var sections = List.of(pageSectionService.findById(id));
        return inertia.render("sections/Show", Map.of("pageSections", sections));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.FOUND)
    public ResponseEntity<String> create(@Valid @RequestBody PageSectionCreateDTO dto) {

        pageSectionService.create(dto);
        return inertia.redirect("/api/pages/sections");
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public ResponseEntity<String> update(@PathVariable Long id,
                                 @Valid @RequestBody PageSectionUpdateDTO dto) {

        pageSectionService.updateById(id, dto);
        return inertia.redirect("/api/pages/sections/" + id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.SEE_OTHER)
    public ResponseEntity<String> delete(@PathVariable Long id) {

        pageSectionService.deleteById(id);
        return inertia.redirect("/api/pages/sections");
    }
}
