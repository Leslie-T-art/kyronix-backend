package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.common.authorization.AuthorizerCandidate;
import com.kyronic.riskengine.common.authorization.AuthorizationStatus;
import com.kyronic.riskengine.common.authorization.SegregationOfDutiesPolicy;
import com.kyronic.riskengine.common.authorization.ServerSideAuthorizerResolver;
import com.kyronic.riskengine.common.events.EventEnvelope;
import com.kyronic.riskengine.olts.application.dto.CreateIncidentRequest;
import com.kyronic.riskengine.olts.application.dto.UpdateIncidentRequest;
import com.kyronic.riskengine.olts.domain.model.OltsIncident;
import com.kyronic.riskengine.olts.domain.model.Severity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OltsIncidentServiceTest {

    @Test
    void submitResolvesAuthorizerAndPublishesEvent() {
        InMemoryStore store = new InMemoryStore();
        CapturingPublisher publisher = new CapturingPublisher();
        UUID departmentId = UUID.randomUUID();
        UUID maker = UUID.randomUUID();

        OltsIncidentService service = new OltsIncidentService(
                () -> "OLTS-2026-00001",
                store,
                new ServerSideAuthorizerResolver(),
                new SegregationOfDutiesPolicy(),
                (ignoredDepartmentId, ignoredPermission) -> List.of(new AuthorizerCandidate(
                        UUID.randomUUID(),
                        departmentId,
                        Set.of("OLTS_AUTHORIZE"),
                        true,
                        false,
                        null,
                        null,
                        null
                )),
                publisher,
                code -> code,
                Clock.fixed(Instant.parse("2026-08-05T08:00:00Z"), ZoneOffset.UTC)
        );

        OltsIncident created = service.create(request(departmentId), maker, "maker", "corr-1");
        OltsIncident submitted = service.submit(created.getIncidentId(), maker, "corr-2");

        assertThat(submitted.getAuthorizationStatus()).isEqualTo(AuthorizationStatus.PENDING_AUTHORIZATION);
        assertThat(publisher.events).extracting(EventEnvelope::eventType)
                .contains("olts.incident.created.v1", "olts.incident.submitted.v1");
    }

    @Test
    void updateAndDeleteWorkForDraftIncident() {
        InMemoryStore store = new InMemoryStore();
        CapturingPublisher publisher = new CapturingPublisher();
        UUID departmentId = UUID.randomUUID();
        UUID maker = UUID.randomUUID();

        OltsIncidentService service = new OltsIncidentService(
                () -> "OLTS-2026-00001",
                store,
                new ServerSideAuthorizerResolver(),
                new SegregationOfDutiesPolicy(),
                (ignoredDepartmentId, ignoredPermission) -> List.of(),
                publisher,
                code -> code,
                Clock.fixed(Instant.parse("2026-08-05T08:00:00Z"), ZoneOffset.UTC)
        );

        service.create(request(departmentId), maker, "maker", "corr-1");
        OltsIncident updated = service.update("OLTS-2026-00001", updateRequest(departmentId), maker, "maker", "corr-2");
        service.delete("OLTS-2026-00001", maker, "corr-3");

        assertThat(updated.getDescription()).isEqualTo("Updated narrative");
        assertThat(service.listAll()).isEmpty();
        assertThat(publisher.events).extracting(EventEnvelope::eventType)
                .contains("olts.incident.updated.v1", "olts.incident.deleted.v1");
    }

    private CreateIncidentRequest request(UUID departmentId) {
        return new CreateIncidentRequest(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                UUID.randomUUID(),
                departmentId,
                "INTERNAL_FRAUD",
                "INCIDENT",
                Severity.HIGH,
                "ATM outage caused unreconciled postings",
                "USD",
                new BigDecimal("100.00"),
                new BigDecimal("5.00"),
                new BigDecimal("25.00")
        );
    }

    private UpdateIncidentRequest updateRequest(UUID departmentId) {
        return new UpdateIncidentRequest(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                UUID.randomUUID(),
                departmentId,
                "PROCESS_FAILURE",
                "OPERATIONAL_LOSS",
                Severity.MEDIUM,
                "Updated narrative",
                "USD",
                new BigDecimal("250.00"),
                new BigDecimal("50.00"),
                new BigDecimal("75.00")
        );
    }

    private static final class InMemoryStore implements OltsIncidentStore {
        private OltsIncident incident;

        @Override
        public OltsIncident save(OltsIncident incident) {
            this.incident = incident;
            return incident;
        }

        @Override
        public Optional<OltsIncident> findByIncidentId(String incidentId) {
            return Optional.ofNullable(incident).filter(saved -> saved.getIncidentId().equals(incidentId));
        }

        @Override
        public List<OltsIncident> findAllActive() {
            return incident == null || incident.isDeleted() ? List.of() : List.of(incident);
        }
    }

    private static final class CapturingPublisher implements EventPublisher {
        private final List<EventEnvelope> events = new ArrayList<>();

        @Override
        public void publish(EventEnvelope eventEnvelope) {
            events.add(eventEnvelope);
        }
    }
}
