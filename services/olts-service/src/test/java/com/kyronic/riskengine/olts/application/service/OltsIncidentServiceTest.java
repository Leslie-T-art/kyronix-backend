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

import static org.assertj.core.api.Assertions.assertThat;

class OltsIncidentServiceTest {

    @Test
    void submitResolvesAuthorizerAndPublishesEvent() {
        InMemoryStore store = new InMemoryStore();
        CapturingPublisher publisher = new CapturingPublisher();
        Long departmentId = 101L;
        Long maker = 1001L;

        OltsIncidentService service = new OltsIncidentService(
                () -> "OLTS-2026-00001",
                store,
                new ServerSideAuthorizerResolver(),
                new SegregationOfDutiesPolicy(),
                (ignoredDepartmentId, ignoredPermission) -> List.of(new AuthorizerCandidate(
                        1002L,
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
        Long departmentId = 101L;
        Long maker = 1001L;

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

        assertThat(updated.getEventDescription()).isEqualTo("Updated narrative");
        assertThat(service.listAll()).isEmpty();
        assertThat(publisher.events).extracting(EventEnvelope::eventType)
                .contains("olts.incident.updated.v1", "olts.incident.deleted.v1");
    }

    private CreateIncidentRequest request(Long departmentId) {
        return new CreateIncidentRequest(
                "ATM Switch Failure",
                11L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                LocalDate.of(2026, 8, 2),
                201L,
                departmentId,
                "Payments",
                "ATM Services",
                21L,
                "ATM outage caused unreconciled postings",
                "Channel switched to manual routing",
                31L,
                "Batch handoff failure",
                41L,
                true,
                51L,
                new BigDecimal("100.00"),
                new BigDecimal("5.00"),
                61L,
                "GL-001",
                71L,
                "Service outage",
                "Branch customers were delayed",
                Severity.HIGH,
                "Implement failover",
                "Head of Operations",
                LocalDate.of(2026, 8, 15),
                81L,
                true,
                "validation-pack.pdf",
                LocalDate.of(2026, 8, 20),
                "Closure pending review"
        );
    }

    private UpdateIncidentRequest updateRequest(Long departmentId) {
        return new UpdateIncidentRequest(
                "ATM Switch Failure",
                12L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 4),
                LocalDate.of(2026, 8, 2),
                201L,
                departmentId,
                "Payments",
                "ATM Services",
                22L,
                "Updated narrative",
                "Manual monitoring introduced",
                32L,
                "Updated root cause",
                42L,
                true,
                52L,
                new BigDecimal("250.00"),
                new BigDecimal("50.00"),
                62L,
                "GL-002",
                72L,
                "Customer impact",
                "Updated details",
                Severity.MEDIUM,
                "Updated corrective action",
                "Treasury Director",
                LocalDate.of(2026, 9, 15),
                82L,
                true,
                "evidence.zip",
                LocalDate.of(2026, 9, 20),
                "Updated closure comment"
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
        public List<OltsIncident> findAll() {
            return incident == null ? List.of() : List.of(incident);
        }

        @Override
        public void delete(OltsIncident incident) {
            this.incident = null;
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
