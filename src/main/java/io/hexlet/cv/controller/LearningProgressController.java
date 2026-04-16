package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.dto.learning.UserLessonProgressDTO;
import io.hexlet.cv.dto.learning.UserProgramProgressDTO;
import io.hexlet.cv.service.UserLessonProgressService;
import io.hexlet.cv.service.UserProgramProgressService;
import io.hexlet.cv.util.UserUtils;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@Slf4j
@AllArgsConstructor
@RequestMapping("/account/my-progress")
public class LearningProgressController {

    private final Inertia inertia;
    private final UserProgramProgressService userProgramProgressService;
    private final UserLessonProgressService userLessonProgressService;
    private final UserUtils userUtils;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Object getProgress(@PageableDefault(size = 10) Pageable pageable) {
        log.debug("Get progress request");
        var user = userUtils.getCurrentUser();

        Page<UserProgramProgressDTO> progressPage = userProgramProgressService.getUserProgress(user.getId(),
                pageable);

        Map<String, Object> props = Map.of(
                "progress", progressPage.getContent(),
                "pagination", Map.of(
                        "currentPage", progressPage.getNumber(),
                        "totalPages", progressPage.getTotalPages(),
                        "totalElements", progressPage.getTotalElements(),
                        "pageSize", pageable.getPageSize()
                ),
                "activeMainSection", "account",
                "activeSubSection", "my-progress"
        );

        log.debug("[CONTROLLER] Rendering Account/Learning/MyProgress/Index with {} "
                        + "programs and pagination",
                progressPage.getContent().size());
        return inertia.render("Account/Learning/MyProgress/Index", props);
    }

    @GetMapping("/program/{programProgressId}/lessons")
    @ResponseStatus(HttpStatus.OK)
    public Object getLessonProgress(@PathVariable Long programProgressId,
                                    @PageableDefault(size = 10) Pageable pageable) {
        var user = userUtils.getCurrentUser();
        log.debug("[CONTROLLER] Getting lessons for programProgressId: {}, user: {}, pageable: {}",
                programProgressId, user.getEmail(), pageable);

        Page<UserLessonProgressDTO> lessonsProgressPage = userLessonProgressService
                .getLessonProgress(user.getId(), programProgressId, pageable);
        Map<String, Object> props = Map.of(
                "lessonsProgress", lessonsProgressPage.getContent(),
                "pagination", Map.of(
                        "currentPage", lessonsProgressPage.getNumber(),
                        "totalPages", lessonsProgressPage.getTotalPages(),
                        "totalElements", lessonsProgressPage.getTotalElements(),
                        "pageSize", pageable.getPageSize()
                ),
                "programProgressId", programProgressId,
                "activeMainSection", "account",
                "activeSubSection", "my-progress"
        );

        log.debug("[CONTROLLER] Rendering a Lessons page {} lessons snd pagination",
                lessonsProgressPage.getContent().size());
        return inertia.render("Account/Learning/MyProgress/Lessons", props);
    }

    @PostMapping("/program/start")
    public Object startProgram(@RequestParam Long programId) {
        var user = userUtils.getCurrentUser();
        log.debug("[CONTROLLER] Program start {} by user {}", programId, user.getEmail());

        userProgramProgressService.startProgram(user.getId(), programId);
        return inertia.redirect("/account/my-progress");
    }

    @PostMapping("/lesson/start")
    public Object startLesson(@RequestParam Long programProgressId,
                              @RequestParam Long lessonId) {

        var user = userUtils.getCurrentUser();
        log.debug("[CONTROLLER] Lesson start {} in the program {} by the user {}",
                lessonId, programProgressId, user.getEmail());

        userLessonProgressService.startLesson(user.getId(), programProgressId, lessonId);
        return inertia.redirect("/account/my-progress/program/" + programProgressId + "/lessons");

    }

    @PostMapping("/lesson/{lessonProgressId}/complete")
    public Object completeLesson(@PathVariable Long lessonProgressId,
                                 @RequestParam Long programProgressId) {
        userLessonProgressService.completeLesson(lessonProgressId);
        return inertia.redirect("/account/my-progress/program/" + programProgressId + "/lessons");
    }

    @PostMapping("/program/{programProgressId}/complete")
    public Object completeProgram(@PathVariable Long programProgressId) {
        userProgramProgressService.completeProgram(programProgressId);
        return inertia.redirect("/account/my-progress");
    }

    @GetMapping("/")
    public Object defaultSection() {
        log.debug("[CONTROLLER] GET /account/my-progress/ - Redirect to the main page");
        return inertia.redirect("/account/my-progress");
    }
}
