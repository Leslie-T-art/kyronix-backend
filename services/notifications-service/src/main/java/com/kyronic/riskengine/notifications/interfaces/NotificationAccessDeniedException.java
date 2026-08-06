package com.kyronic.riskengine.notifications.interfaces;

import java.util.UUID;

public class NotificationAccessDeniedException extends RuntimeException {

    public NotificationAccessDeniedException(UUID notificationId) {
        super(notificationId == null
                ? "Notification access denied"
                : "Notification access denied: " + notificationId);
    }
}
