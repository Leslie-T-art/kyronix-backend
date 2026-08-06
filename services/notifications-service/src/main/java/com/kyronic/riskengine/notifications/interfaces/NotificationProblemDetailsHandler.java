package com.kyronic.riskengine.notifications.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class NotificationProblemDetailsHandler {

    @ExceptionHandler(NotificationNotFoundException.class)
    ProblemDetail handleNotFound(NotificationNotFoundException exception, HttpServletRequest request) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Notification not found");
        detail.setDetail(exception.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty("errorCode", "NOTIFICATION_NOT_FOUND");
        return detail;
    }

    @ExceptionHandler(NotificationAccessDeniedException.class)
    ProblemDetail handleAccessDenied(NotificationAccessDeniedException exception, HttpServletRequest request) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setTitle("Notification access denied");
        detail.setDetail(exception.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty("errorCode", "NOTIFICATION_ACCESS_DENIED");
        return detail;
    }

    @ExceptionHandler(InvalidActionUrlException.class)
    ProblemDetail handleInvalidActionUrl(InvalidActionUrlException exception, HttpServletRequest request) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Invalid action URL");
        detail.setDetail(exception.getMessage());
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty("errorCode", "INVALID_ACTION_URL");
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation failed");
        detail.setDetail("One or more fields failed validation");
        detail.setInstance(URI.create(request.getRequestURI()));
        detail.setProperty("errorCode", "VALIDATION_ERROR");
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        detail.setProperty("fieldErrors", fieldErrors);
        return detail;
    }
}
