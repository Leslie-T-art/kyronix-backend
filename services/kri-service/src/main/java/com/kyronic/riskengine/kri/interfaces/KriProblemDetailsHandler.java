package com.kyronic.riskengine.kri.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class KriProblemDetailsHandler {

    @ExceptionHandler(KriNotFoundException.class)
    ProblemDetail handleNotFound(KriNotFoundException exception, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle("KRI not found");
        problemDetail.setDetail(exception.getMessage());
        problemDetail.setInstance(java.net.URI.create(request.getRequestURI()));
        problemDetail.setProperty("errorCode", "KRI_NOT_FOUND");
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation failed");
        problemDetail.setDetail("One or more fields failed validation");
        problemDetail.setInstance(java.net.URI.create(request.getRequestURI()));
        problemDetail.setProperty("errorCode", "VALIDATION_ERROR");
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problemDetail.setProperty("fieldErrors", fieldErrors);
        return problemDetail;
    }
}
