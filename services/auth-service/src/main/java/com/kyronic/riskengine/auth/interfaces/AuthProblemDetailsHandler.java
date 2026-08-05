package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.service.AuditRequestFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthProblemDetailsHandler {

    @ExceptionHandler(BadCredentialsException.class)
    ProblemDetail handleBadCredentials(BadCredentialsException exception, HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed", exception.getMessage(), "INVALID_CREDENTIALS", request);
    }

    @ExceptionHandler(LockedException.class)
    ProblemDetail handleLocked(LockedException exception, HttpServletRequest request) {
        return problem(HttpStatus.FORBIDDEN, "Account locked", exception.getMessage(), "ACCOUNT_LOCKED", request);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ProblemDetail handleValidation(Exception exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Validation failed", exception.getMessage(), "VALIDATION_ERROR", request);
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail, String errorCode, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setProperty("errorCode", errorCode);
        Object correlationId = request.getAttribute(AuditRequestFactory.CORRELATION_ID_ATTRIBUTE);
        if (correlationId instanceof String value) {
            problemDetail.setProperty("correlationId", value);
        }
        return problemDetail;
    }
}
