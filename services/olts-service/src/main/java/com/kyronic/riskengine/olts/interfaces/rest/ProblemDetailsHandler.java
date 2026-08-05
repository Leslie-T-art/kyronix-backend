package com.kyronic.riskengine.olts.interfaces.rest;

import com.kyronic.riskengine.common.authorization.AuthorizationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProblemDetailsHandler {

    @ExceptionHandler(AuthorizationException.class)
    public ProblemDetail handleAuthorization(AuthorizationException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setTitle("Segregation of duties violation");
        detail.setDetail(exception.getMessage());
        detail.setProperty("errorCode", exception.getErrorCode());
        return detail;
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(org.springframework.security.access.AccessDeniedException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        detail.setTitle("Access denied");
        detail.setDetail(exception.getMessage());
        detail.setProperty("errorCode", "ACCESS_DENIED");
        return detail;
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ProblemDetail handleValidation(Exception exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation failed");
        detail.setDetail(exception.getMessage());
        detail.setProperty("errorCode", "VALIDATION_ERROR");
        return detail;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Validation failed");
        detail.setDetail("The request contains invalid data for one or more persisted fields");
        detail.setProperty("errorCode", "DATA_INTEGRITY_VIOLATION");
        return detail;
    }
}
