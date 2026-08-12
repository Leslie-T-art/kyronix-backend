package com.kyronic.riskengine.processflows.interfaces;

public class ProcessFlowNotFoundException extends RuntimeException {
    public ProcessFlowNotFoundException(Long id) {
        super("Process flow not found: " + id);
    }
}
