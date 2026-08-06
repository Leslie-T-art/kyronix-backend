package com.kyronic.riskengine.riskregister.interfaces;

public class RiskRecordNotFoundException extends RuntimeException {

    public RiskRecordNotFoundException(String riskId) {
        super("Risk record not found: " + riskId);
    }
}
