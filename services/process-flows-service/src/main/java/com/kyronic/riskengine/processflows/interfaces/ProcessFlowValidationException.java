package com.kyronic.riskengine.processflows.interfaces;

public class ProcessFlowValidationException extends RuntimeException {

    private final String field;

    public ProcessFlowValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String field() {
        return field;
    }
}
