package com.kyronic.riskengine.notifications.application.service;

import com.kyronic.riskengine.notifications.application.dto.NotificationEventRequest;
import com.kyronic.riskengine.notifications.interfaces.InvalidActionUrlException;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class NotificationActionUrlFactory {

    private static final Set<String> APPROVED_PREFIXES = Set.of(
            "/olts",
            "/kri",
            "/risks",
            "/process-flows",
            "/self-assessments",
            "/audits",
            "/notifications"
    );

    public String actionUrl(NotificationEventRequest event) {
        String path = switch (event.sourceService()) {
            case "olts-service" -> "/olts/incidents/" + event.businessReference();
            case "kri-service" -> "/kri/records/" + event.businessReference();
            case "risk-register-service" -> "/risks/" + event.businessReference();
            case "process-flows-service" -> "/process-flows/" + event.businessReference();
            case "self-assessment-service" -> "/self-assessments/" + event.businessReference();
            default -> "/notifications";
        };

        if (event.type().name().contains("AUTHORIZATION")) {
            path = path + "/authorization";
        }

        validate(path);
        return path;
    }

    public void validate(String actionUrl) {
        boolean approved = APPROVED_PREFIXES.stream().anyMatch(actionUrl::startsWith);
        if (!approved) {
            throw new InvalidActionUrlException(actionUrl);
        }
    }
}
