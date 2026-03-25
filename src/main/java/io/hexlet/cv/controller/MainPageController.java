package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.service.PageSectionService;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@AllArgsConstructor
@RequestMapping({"/"})
public class MainPageController {

    private final Inertia inertia;
    private final PageSectionService pageSectionService;

    private static final String PAGE_KEY = "main";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> index() {

        var sections = pageSectionService.findAllOnPage(PAGE_KEY, true);

        Map<String, Object> props = Map.of(
            "pageSections", sections
        );

        return inertia.render("Home/Index", props);
    }
}
