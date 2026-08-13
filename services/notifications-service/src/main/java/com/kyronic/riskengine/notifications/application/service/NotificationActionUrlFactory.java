package com.kyronic.riskengine.notifications.application.service;

import com.kyronic.riskengine.notifications.application.dto.NotificationEventRequest;
import com.kyronic.riskengine.notifications.interfaces.InvalidActionUrlException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class NotificationActionUrlFactory {

    private static final Map<String, String> SERVICE_PATHS = Map.ofEntries(
            Map.entry("api-gateway", "/dashboard"),
            Map.entry("audit-service", "/audits"),
            Map.entry("auth-service", "/auth"),
            Map.entry("dashboard-service", "/dashboard"),
            Map.entry("document-service", "/documents"),
            Map.entry("kri-service", "/kri/records"),
            Map.entry("notifications-service", "/notifications"),
            Map.entry("olts-service", "/olts/incidents"),
            Map.entry("process-flows-service", "/process-flows"),
            Map.entry("risk-register-service", "/risks"),
            Map.entry("self-assessment-service", "/self-assessments")
    );

    private static final Set<String> APPROVED_PREFIXES = Set.of(
            "/auth",
            "/dashboard",
            "/documents",
            "/olts",
            "/kri",
            "/risks",
            "/process-flows",
            "/self-assessments",
            "/audits",
            "/notifications"
    );

    public String actionUrl(NotificationEventRequest event) {
        String basePath = SERVICE_PATHS.getOrDefault(event.sourceService(), "/notifications");
        String path = event.businessReference() == null || event.businessReference().isBlank()
                ? basePath
                : basePath + "/" + event.businessReference();

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
