package com.kyronic.riskengine.kri.interfaces;

public class TreatmentStrategyNotFoundException extends RuntimeException {

    public TreatmentStrategyNotFoundException(Long id) {
        super("Treatment strategy not found: " + id);
    }
}
