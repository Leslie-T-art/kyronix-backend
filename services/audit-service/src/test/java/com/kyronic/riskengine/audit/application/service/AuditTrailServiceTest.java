package com.kyronic.riskengine.audit.application.service;

import com.kyronic.riskengine.audit.domain.AuditTrailEntry;
import com.kyronic.riskengine.audit.infrastructure.persistence.AuditTrailRepository;
import com.kyronic.riskengine.common.observability.AuditTrailEntryRequest;
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
    void recordsPlatformAuditEntry() {
        List<AuditTrailEntry> storedEntries = new ArrayList<>();
        AuditTrailRepository repository = repository(storedEntries);
        AuditTrailService service = new AuditTrailService(repository);

        var response = service.record(new AuditTrailEntryRequest(
                "olts-service",
                "HTTP_REQUEST",
                "POST /api/v1/olts/incidents",
                "POST",
                "/api/v1/olts/incidents",
                null,
                201,
                "SUCCESS",
                "risk.inputter",
                "11111111-1111-1111-1111-111111111111",
                "127.0.0.1",
                "JUnit",
                "corr-1",
                Instant.parse("2026-08-05T09:00:00Z")
        ));

        assertThat(response.serviceName()).isEqualTo("olts-service");
        assertThat(response.requestPath()).isEqualTo("/api/v1/olts/incidents");
        assertThat(storedEntries).hasSize(1);
    }

    @Test
    void returnsCrossServiceTrailUsingFiltersAndDescendingOrder() {
        List<AuditTrailEntry> storedEntries = new ArrayList<>();
        storedEntries.add(new AuditTrailEntry(
                UUID.randomUUID(),
                "auth-service",
                "HTTP_REQUEST",
                "POST /api/v1/auth/login",
                "POST",
                "/api/v1/auth/login",
                null,
                200,
                "SUCCESS",
                "system.admin",
                "33333333-3333-3333-3333-333333333333",
                "127.0.0.1",
                "JUnit",
                "corr-2",
                Instant.parse("2026-08-05T08:00:00Z")
        ));
        storedEntries.add(new AuditTrailEntry(
                UUID.randomUUID(),
                "olts-service",
                "HTTP_REQUEST",
                "GET /api/v1/olts/incidents",
                "GET",
                "/api/v1/olts/incidents",
                "page=0",
                200,
                "SUCCESS",
                "risk.inputter",
                "11111111-1111-1111-1111-111111111111",
                "127.0.0.1",
                "JUnit",
                "corr-3",
                Instant.parse("2026-08-05T09:00:00Z")
        ));
        storedEntries.add(new AuditTrailEntry(
                UUID.randomUUID(),
                "notifications-service",
                "HTTP_REQUEST",
                "GET /api/v1/notifications/test-audit",
                "GET",
                "/api/v1/notifications/test-audit",
                null,
                404,
                "REJECTED",
                "risk.inputter",
                "11111111-1111-1111-1111-111111111111",
                "127.0.0.1",
                "JUnit",
                "corr-4",
                Instant.parse("2026-08-05T10:00:00Z")
        ));

        AuditTrailService service = new AuditTrailService(repository(storedEntries));

        var results = service.listTrail("olts-service", "risk.inputter", "SUCCESS", "/api/v1/olts", "incidents", 10);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).serviceName()).isEqualTo("olts-service");
        assertThat(results.get(0).occurredAt()).isEqualTo(Instant.parse("2026-08-05T09:00:00Z"));
    }

    @Test
    void limitsResultsToRequestedWindow() {
        List<AuditTrailEntry> storedEntries = new ArrayList<>();
        storedEntries.add(entry("olts-service", "/api/v1/olts/incidents/1", Instant.parse("2026-08-05T09:00:00Z")));
        storedEntries.add(entry("olts-service", "/api/v1/olts/incidents/2", Instant.parse("2026-08-05T10:00:00Z")));

        AuditTrailService service = new AuditTrailService(repository(storedEntries));

        var results = service.listTrail(null, null, null, null, null, 1);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).requestPath()).isEqualTo("/api/v1/olts/incidents/2");
    }

    private AuditTrailEntry entry(String serviceName, String requestPath, Instant occurredAt) {
        return new AuditTrailEntry(
                UUID.randomUUID(),
                serviceName,
                "HTTP_REQUEST",
                "GET " + requestPath,
                "GET",
                requestPath,
                null,
                200,
                "SUCCESS",
                "risk.inputter",
                "11111111-1111-1111-1111-111111111111",
                "127.0.0.1",
                "JUnit",
                UUID.randomUUID().toString(),
                occurredAt
        );
    }

    @SuppressWarnings("unchecked")
    private AuditTrailRepository repository(List<AuditTrailEntry> storedEntries) {
        return (AuditTrailRepository) Proxy.newProxyInstance(
                AuditTrailRepository.class.getClassLoader(),
                new Class<?>[]{AuditTrailRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        AuditTrailEntry entry = (AuditTrailEntry) args[0];
                        storedEntries.add(entry);
                        yield entry;
                    }
                    case "findAll" -> {
                        if (args.length == 1 && args[0] instanceof Sort) {
                            yield storedEntries.stream()
                                    .sorted((left, right) -> right.getOccurredAt().compareTo(left.getOccurredAt()))
                                    .toList();
                        }
                        throw new UnsupportedOperationException("findAll signature");
                    }
                    case "toString" -> "FakeAuditTrailRepository";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }
}
