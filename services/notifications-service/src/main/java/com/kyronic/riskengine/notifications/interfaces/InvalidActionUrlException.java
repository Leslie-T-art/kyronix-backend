package com.kyronic.riskengine.notifications.interfaces;

public class InvalidActionUrlException extends RuntimeException {

    public InvalidActionUrlException(String actionUrl) {
        super("Invalid action URL: " + actionUrl);
    }
}
