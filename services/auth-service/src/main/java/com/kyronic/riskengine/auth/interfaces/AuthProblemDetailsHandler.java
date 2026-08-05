package com.kyronic.riskengine.auth.interfaces;

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
    ProblemDetail handleBadCredentials(BadCredentialsException exception) {
        return problem(HttpStatus.UNAUTHORIZED, "Authentication failed", exception.getMessage(), "INVALID_CREDENTIALS");
    }

    @ExceptionHandler(LockedException.class)
    ProblemDetail handleLocked(LockedException exception) {
        return problem(HttpStatus.FORBIDDEN, "Account locked", exception.getMessage(), "ACCOUNT_LOCKED");
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ProblemDetail handleValidation(Exception exception) {
        return problem(HttpStatus.BAD_REQUEST, "Validation failed", exception.getMessage(), "VALIDATION_ERROR");
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail, String errorCode) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle(title);
        problemDetail.setDetail(detail);
        problemDetail.setProperty("errorCode", errorCode);
        return problemDetail;
    }
}
