package io.hexlet.cv.controller;

import io.github.inertia4j.spring.Inertia;
import io.hexlet.cv.handler.exception.UserNotFoundException;
import io.hexlet.cv.service.FlashPropsService;
import io.hexlet.cv.service.UserPageSercive;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@AllArgsConstructor
public class UserPageController {

    private final Inertia inertia;
    private final FlashPropsService flashPropsService;
    private final UserPageSercive userPageService;

    @GetMapping("/users/{user_id}")
    public ResponseEntity<?> userPage(
            @PathVariable("user_id") Long userId,
            HttpServletRequest request) {

        try {

            Map<String, Object> props = flashPropsService.buildProps(request);
            Map<String, Object> userPageProps = userPageService.buildProps(userId);

            props.putAll(userPageProps);
            return inertia.render("Users/UserPage", props);

        } catch (UserNotFoundException ex) {
            Map<String, Object> errorProps = flashPropsService.buildProps(request);
            errorProps.put("status", 404);
            errorProps.put("userId", userId);

            ResponseEntity<?> inertiaResponse = inertia.render("Error/UserNotFound", errorProps);

            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(inertiaResponse.getHeaders())
                    .body(inertiaResponse.getBody());
        }
    }
}
