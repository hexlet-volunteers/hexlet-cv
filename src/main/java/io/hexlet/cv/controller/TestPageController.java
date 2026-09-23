package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class TestPageController {

    private final Inertia inertia;

    @GetMapping("/test")
    public ResponseEntity<?> testPage() {

        return ResponseEntity.ok(
                inertia.render("Home/TestPage", Map.of("text", "test page")));
    }
}
