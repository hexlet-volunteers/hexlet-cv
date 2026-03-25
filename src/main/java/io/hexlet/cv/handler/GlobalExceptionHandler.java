package io.hexlet.cv.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.hexlet.cv.handler.exception.ClientException;
import io.hexlet.cv.handler.exception.InvalidPasswordException;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.handler.exception.ServerException;
import io.hexlet.cv.handler.exception.UserAlreadyExistsException;
import io.hexlet.cv.handler.exception.UserNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private Object commonHandle(Map<String, String> errors,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes,
                                HttpStatus status) {

        // Обработка AJAX-запроса (Inertia)
        if ("true".equals(request.getHeader("X-Inertia"))) {
            redirectAttributes.addFlashAttribute("errors", errors);
            String referer = request.getHeader("Referer");
            RedirectView redirectView = new RedirectView(referer != null ? referer : "/");
            redirectView.setHttp10Compatible(false);
            redirectView.setStatusCode(HttpStatus.SEE_OTHER);
            return redirectView;
        }

        // Обработка обычного запроса
        return ResponseEntity.status(status).body(Map.of("errors", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                               HttpServletRequest request,
                                               RedirectAttributes redirectAttributes) {

        String errorMessage = "Invalid JSON format";

        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) ex.getCause();
            if (cause.getTargetType().isEnum()) {
                Class<? extends Enum> enumClass = (Class<? extends Enum>) cause.getTargetType();
                String fieldName = cause.getPath().isEmpty() ? "unknown" : cause.getPath().get(0).getFieldName();
                errorMessage = String.format("Invalid value '%s' for %s. Allowed values: %s",
                        cause.getValue(),
                        fieldName,
                        Arrays.toString(enumClass.getEnumConstants()));
            }
        }

        Map<String, String> errors = Map.of("error", errorMessage);
        return commonHandle(errors, request, redirectAttributes, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityExistsException.class)
    public Object handleEntityExistsException(EntityExistsException ex,
                                              HttpServletRequest request,
                                              RedirectAttributes redirectAttributes) {

        Map<String, String> errors = Map.of("error", ex.getMessage());
        return commonHandle(errors, request, redirectAttributes, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException ex,
                                                  HttpServletRequest request,
                                                  RedirectAttributes redirectAttributes) {

        Map<String, String> errors = Map.of("error", ex.getMessage());
        return commonHandle(errors, request, redirectAttributes, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        return commonHandle(errors, request, redirectAttributes, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public Object handleUserAlreadyExists(UserAlreadyExistsException ex,
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {

        Map<String, String> errors = Map.of("email", ex.getMessage());
        return commonHandle(errors, request, redirectAttributes, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Object handleUserNotFound(UserNotFoundException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {

        Map<String, String> errors = Map.of("email", ex.getMessage());
        return commonHandle(errors, request, redirectAttributes, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public Object handleInvalidPasswordException(InvalidPasswordException ex,
                                                 HttpServletRequest request,
                                                 RedirectAttributes redirectAttributes) {

        Map<String, String> errors = Map.of("password", ex.getMessage());
        return commonHandle(errors, request, redirectAttributes, HttpStatus.UNAUTHORIZED);
    }

// это просто ошибки все остальное
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        Map<String, String> errors = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("errors", errors));
    }


    //400 и 500 ошибки
    @ExceptionHandler(ClientException.class)
    public Object handleClientException(ClientException ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {

        Map<String, String> errors = Map.of(ex.getField(), ex.getMessage());
        return commonHandle(errors, request, redirectAttributes, ex.getStatus());
    }

    @ExceptionHandler(ServerException.class)
    public Object handleServerException(ServerException ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {

        log.error("Server error: {} - {}", ex.getErrorCode(), ex.getMessage(), ex);

        Map<String, String> errors = Map.of("error",
                "A technical error occurred. Please try again later.");

        return commonHandle(errors, request, redirectAttributes,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDenied(AccessDeniedException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        Map<String, String> errors = Map.of("auth", ex.getMessage());

        return commonHandle(errors, request, redirectAttributes, HttpStatus.FORBIDDEN);
    }

}
