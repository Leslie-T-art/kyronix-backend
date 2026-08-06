package com.kyronic.riskengine.kri.interfaces;

public class KriNotFoundException extends RuntimeException {

    public KriNotFoundException(String kriId) {
        super("KRI not found: " + kriId);
    }
}
