package com.kyronic.riskengine.auth.application.service;

import com.kyronic.riskengine.auth.application.dto.AuditEventCommand;
import com.kyronic.riskengine.auth.domain.AuditEvent;
import com.kyronic.riskengine.auth.infrastructure.persistence.AuditEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Proxy;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditTrailServiceTest {

    @Test
    void recordsAuditEventsWithAuthServiceName() {
        List<AuditEvent> storedEvents = new ArrayList<>();
        AuditEventRepository repository = repository(storedEvents);
        AuditTrailService service = new AuditTrailService(repository);

        var response = service.record(new AuditEventCommand(
                "AUTH_LOGIN_SUCCESS",
                "LOGIN",
                "AUTH_SESSION",
                null,
                null,
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "risk.inputter",
                "INPUTTER",
                "OLTS_CREATE",
                "SUCCESS",
                null,
                "POST",
                "/api/v1/auth/login",
                "127.0.0.1",
                "JUnit",
                "corr-1",
                null,
                "{\"username\":\"risk.inputter\"}",
                Instant.parse("2026-08-05T08:30:00Z")
        ));

        assertThat(response.serviceName()).isEqualTo("auth-service");
        assertThat(response.eventType()).isEqualTo("AUTH_LOGIN_SUCCESS");
        assertThat(storedEvents).hasSize(1);
    }

    @Test
    void returnsAuditEventsInDescendingOrder() {
        List<AuditEvent> storedEvents = new ArrayList<>();
        storedEvents.add(
                new AuditEvent(
                        UUID.randomUUID(),
                        "ADMIN_USER_CREATED",
                        "CREATE_USER",
                        "auth-service",
                        "USER_ACCOUNT",
                        "123",
                        "risk.inputter",
                        UUID.fromString("33333333-3333-3333-3333-333333333333"),
                        "system.admin",
                        "SYSTEM_ADMIN",
                        "ADMIN_USERS",
                        "SUCCESS",
                        null,
                        "POST",
                        "/api/v1/admin/users",
                        "127.0.0.1",
                        "JUnit",
                        "corr-2",
                        null,
                        "{\"username\":\"new.user\"}",
                        Instant.parse("2026-08-05T09:00:00Z")
                )
        );
        AuditEventRepository repository = repository(storedEvents);
        AuditTrailService service = new AuditTrailService(repository);

        var results = service.findAuditEvents();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).username()).isEqualTo("system.admin");
    }

    @SuppressWarnings("unchecked")
    private AuditEventRepository repository(List<AuditEvent> storedEvents) {
        return (AuditEventRepository) Proxy.newProxyInstance(
                AuditEventRepository.class.getClassLoader(),
                new Class<?>[]{AuditEventRepository.class},
                (proxy, method, args) -> {
                    return switch (method.getName()) {
                        case "save" -> {
                            AuditEvent event = (AuditEvent) args[0];
                            storedEvents.add(event);
                            yield event;
                        }
                        case "findAll" -> {
                            if (args.length == 1 && args[0] instanceof Sort) {
                                yield storedEvents;
                            }
                            throw new UnsupportedOperationException("findAll signature");
                        }
                        case "count" -> (long) storedEvents.size();
                        case "toString" -> "FakeAuditEventRepository";
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy == args[0];
                        default -> throw new UnsupportedOperationException(method.getName());
                    };
                }
        );
    }
}
