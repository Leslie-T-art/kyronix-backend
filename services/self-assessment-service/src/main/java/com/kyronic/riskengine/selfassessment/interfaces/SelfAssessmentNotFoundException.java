package com.kyronic.riskengine.selfassessment.interfaces;

public class SelfAssessmentNotFoundException extends RuntimeException {
    public SelfAssessmentNotFoundException(Long id) {
        super("Self assessment not found: " + id);
    }
}
