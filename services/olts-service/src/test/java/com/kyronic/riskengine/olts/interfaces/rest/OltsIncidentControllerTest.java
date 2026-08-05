package com.kyronic.riskengine.olts.interfaces.rest;

import com.kyronic.riskengine.common.authorization.AuthorizerCandidate;
import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.CreateIncidentRequest;
import com.kyronic.riskengine.olts.application.dto.IncidentResponse;
import com.kyronic.riskengine.olts.application.mapper.IncidentMapperImpl;
import com.kyronic.riskengine.olts.application.service.OltsIncidentService;
import com.kyronic.riskengine.olts.domain.model.EventType;
import com.kyronic.riskengine.olts.domain.model.LossCategory;
import com.kyronic.riskengine.olts.domain.model.Severity;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OltsIncidentControllerTest {

    private OltsIncidentController controller;
    private Jwt jwt;
    private Validator validator;

    @BeforeEach
    void setUp() {
        InMemoryStore store = new InMemoryStore();
        OltsIncidentService service = new OltsIncidentService(
                () -> "OLTS-2026-00001",
                store,
                new com.kyronic.riskengine.common.authorization.ServerSideAuthorizerResolver(),
                new com.kyronic.riskengine.common.authorization.SegregationOfDutiesPolicy(),
                (departmentId, permission) -> List.of(new AuthorizerCandidate(UUID.randomUUID(), departmentId, Set.of(permission), true, false, null, null, null)),
                event -> {
                },
                Clock.fixed(Instant.parse("2026-08-05T08:00:00Z"), ZoneOffset.UTC)
        );
        jwt = Jwt.withTokenValue("token")
                .subject("risk.inputter")
                .claim("userId", "11111111-1111-1111-1111-111111111111")
                .header("alg", "HS256")
                .build();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        controller = new OltsIncidentController(service, new IncidentMapperImpl());
    }

    @Test
    void createsIncident() {
        UUID departmentId = UUID.randomUUID();
        CreateIncidentRequest request = new CreateIncidentRequest(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                UUID.randomUUID(),
                departmentId,
                LossCategory.INTERNAL_FRAUD,
                EventType.INCIDENT,
                Severity.HIGH,
                "System outage detected by branch operations",
                "USD",
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00")
        );

        ApiResponse<IncidentResponse> response = controller.create(request, jwt);

        assertThat(response.success()).isTrue();
        assertThat(response.data().incidentId()).isEqualTo("OLTS-2026-00001");
        assertThat(response.data().inputterUserId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(response.data().responsiblePersonId()).isEqualTo(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertThat(response.data().responsiblePersonName()).isEqualTo("risk.inputter");
        assertThat(response.data().netLoss()).isEqualByComparingTo("90.00");
    }

    @Test
    void listsIncidents() {
        UUID departmentId = UUID.randomUUID();
        CreateIncidentRequest request = new CreateIncidentRequest(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                UUID.randomUUID(),
                departmentId,
                LossCategory.INTERNAL_FRAUD,
                EventType.INCIDENT,
                Severity.HIGH,
                "System outage detected by branch operations",
                "USD",
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00")
        );

        controller.create(request, jwt);

        ApiResponse<List<IncidentResponse>> response = controller.list();

        assertThat(response.success()).isTrue();
        assertThat(response.data()).hasSize(1);
    }

    @Test
    void rejectsInvalidCurrencyCodeAtRequestBoundary() {
        CreateIncidentRequest request = new CreateIncidentRequest(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                UUID.randomUUID(),
                UUID.randomUUID(),
                LossCategory.INTERNAL_FRAUD,
                EventType.INCIDENT,
                Severity.HIGH,
                "System outage detected by branch operations",
                "string",
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00")
        );

        assertThat(validator.validate(request)).isNotEmpty();
    }

    private static final class InMemoryStore implements com.kyronic.riskengine.olts.application.service.OltsIncidentStore {
        private com.kyronic.riskengine.olts.domain.model.OltsIncident incident;

        @Override
        public com.kyronic.riskengine.olts.domain.model.OltsIncident save(com.kyronic.riskengine.olts.domain.model.OltsIncident incident) {
            this.incident = incident;
            return incident;
        }

        @Override
        public Optional<com.kyronic.riskengine.olts.domain.model.OltsIncident> findByIncidentId(String incidentId) {
            return Optional.ofNullable(incident).filter(saved -> saved.getIncidentId().equals(incidentId));
        }

        @Override
        public List<com.kyronic.riskengine.olts.domain.model.OltsIncident> findAllActive() {
            return incident == null || incident.isDeleted() ? List.of() : List.of(incident);
        }
    }
}
