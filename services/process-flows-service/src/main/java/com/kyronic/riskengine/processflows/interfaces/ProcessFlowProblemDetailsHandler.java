package com.kyronic.riskengine.processflows.interfaces;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.common.api.ValidationErrorResponse;
import com.kyronic.riskengine.common.api.ValidationErrorResponse.FieldViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ProcessFlowProblemDetailsHandler {

    @ExceptionHandler(ProcessFlowNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ApiResponse<Void> handleNotFound(ProcessFlowNotFoundException exception) {
        return new ApiResponse<>(false, exception.getMessage(), null, java.time.Instant.now(), null);
    }

    @ExceptionHandler(ProcessFlowValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ValidationErrorResponse handleValidation(ProcessFlowValidationException exception) {
        return ValidationErrorResponse.of(
                "Validation failed",
                List.of(new FieldViolation(exception.field(), "INVALID_VALUE", exception.getMessage())),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ValidationErrorResponse handleBeanValidation(MethodArgumentNotValidException exception) {
        List<FieldViolation> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new FieldViolation(error.getField(), "INVALID_VALUE", error.getDefaultMessage()))
                .toList();
        return ValidationErrorResponse.of("Validation failed", errors, null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    ValidationErrorResponse handleConstraintViolation(ConstraintViolationException exception) {
        List<FieldViolation> errors = exception.getConstraintViolations().stream()
                .map(violation -> new FieldViolation(violation.getPropertyPath().toString(), "INVALID_VALUE", violation.getMessage()))
                .toList();
        return ValidationErrorResponse.of("Validation failed", errors, null);
    }
}
