package com.kyronic.riskengine.common.api;

import java.time.Instant;
import java.util.List;

public record ValidationErrorResponse(
        boolean success,
        String message,
        List<FieldViolation> errors,
        Instant timestamp,
        String correlationId
) {
    public record FieldViolation(
            String field,
            String code,
            String message
    ) {
    }

    public static ValidationErrorResponse of(String message, List<FieldViolation> errors, String correlationId) {
        return new ValidationErrorResponse(false, message, errors, Instant.now(), correlationId);
    }
}
