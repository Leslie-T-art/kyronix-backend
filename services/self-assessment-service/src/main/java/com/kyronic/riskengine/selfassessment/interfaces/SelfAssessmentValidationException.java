package com.kyronic.riskengine.selfassessment.interfaces;

public class SelfAssessmentValidationException extends RuntimeException {

    private final String field;

    public SelfAssessmentValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
