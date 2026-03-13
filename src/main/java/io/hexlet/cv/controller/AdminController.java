
package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.service.FlashPropsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@AllArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final Inertia inertia;
    private final FlashPropsService flashPropsService;

    @GetMapping()
    public ResponseEntity<?> adminHomeController(HttpServletRequest request) {

        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Index", props);
    }

    @GetMapping("/marketing")
    public ResponseEntity<?> adminMarketingController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Marketing/Index", props);
    }

    @GetMapping("/webinars")
    public ResponseEntity<?> adminWebinarsController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Webinars/Index", props);
    }

    @GetMapping("/knowledgebase")
    public ResponseEntity<?> adminKnowledgebaseController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Knowledgebase/Index", props);

    }

    @GetMapping("/interview")
    public ResponseEntity<?> adminInterviewController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Interview/Index", props);

    }

    @GetMapping("/grading")
    public ResponseEntity<?> adminGradingController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Grading/Index", props);
    }

    @GetMapping("/programs")
    public ResponseEntity<?> adminProgramsController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Programs/Index", props);
    }

    @GetMapping("/users")
    public ResponseEntity<?> adminUsersController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Users/Index", props);
    }

    @GetMapping("/settings")
    public ResponseEntity<?> adminSettingsController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Settings/Index", props);
    }

    @GetMapping("/help")
    public ResponseEntity<?> adminHelpController(HttpServletRequest request) {
        var props = flashPropsService.buildProps(request);
        return inertia.render("Admin/Help/Index", props);
    }
}
