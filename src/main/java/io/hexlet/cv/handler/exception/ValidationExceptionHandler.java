package io.hexlet.cv.handler.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        // если заголовок "X-Inertia: true" то это Inertia
        if ("true".equals(request.getHeader("X-Inertia"))) {
            redirectAttributes.addFlashAttribute("errors", errors);
            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/");
        }

        // Если это API-запрос (не Inertia), возвращаем JSON с ошибками и 400 статусом
        return ResponseEntity.badRequest().body(Map.of("errors", errors));
    }
}
