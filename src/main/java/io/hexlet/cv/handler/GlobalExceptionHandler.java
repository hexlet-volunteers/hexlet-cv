package io.hexlet.cv.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.hexlet.cv.audit.AuditEventType;
import io.hexlet.cv.audit.AuditLogger;
import io.hexlet.cv.audit.AuditReason;
import io.hexlet.cv.audit.AuditSubject;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.hexlet.cv.handler.exception.InvalidPasswordException;
import io.hexlet.cv.handler.exception.ResourceNotFoundException;
import io.hexlet.cv.handler.exception.UserAlreadyExistsException;
import io.hexlet.cv.handler.exception.UserNotFoundException;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@ControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final AuditLogger auditLogger;

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

        if (ex.getCause() instanceof UnrecognizedPropertyException cause) {

            errorMessage = String.format(
                    "Unknown property '%s' is not allowed",
                    cause.getPropertyName()
            );

        } else if (ex.getCause() instanceof InvalidFormatException) {
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

    @ExceptionHandler(NoResourceFoundException.class)
    public Object handleNoResourceFound(NoResourceFoundException ex,
                                        HttpServletRequest request,
                                        RedirectAttributes redirectAttributes) {

        Map<String, String> errors = Map.of("error", "Resource not found");
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

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }

        return commonHandle(errors, request, redirectAttributes, HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object handleAccessDenied(AccessDeniedException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        auditLogger.logFailure(AuditEventType.ACCESS_DENIED, AuditSubject.current(),
                AuditReason.FORBIDDEN, request);
        Map<String, String> errors = Map.of("Access denied error", ex.getMessage());
        if ("true".equals(request.getHeader("X-Inertia"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("errors", errors));
        }
        return commonHandle(errors, request, redirectAttributes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handlePropertyReferenceException(
            PropertyReferenceException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        Map<String, String> errors = Map.of(
                "error",
                "Invalid sort parameter"
        );

        return commonHandle(
                errors,
                request,
                redirectAttributes,
                HttpStatus.BAD_REQUEST
        );
    }

// это просто ошибки все остальное
    @ExceptionHandler(Exception.class)
    public Object handleAll(Exception ex,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        log.error("Internal server error", ex);

        auditLogger.logFailure(AuditEventType.UNHANDLED_ERROR, AuditSubject.current(),
                AuditReason.SERVER_ERROR, request);

        Map<String, String> errors = Map.of("error", "Internal server error");
        return commonHandle(errors, request, redirectAttributes, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
