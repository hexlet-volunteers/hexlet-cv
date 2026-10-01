package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.service.FlashPropsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final Inertia inertia;
    private final FlashPropsService flashPropsService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(HttpServletRequest request) {


        var props = flashPropsService.buildProps(request);

        return inertia.render("Dashboard/Index", props);
    }
}
