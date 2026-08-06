package com.kyronic.riskengine.olts.interfaces.rest;

import com.kyronic.riskengine.common.authorization.AuthorizerCandidate;
import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.CreateIncidentRequest;
import com.kyronic.riskengine.olts.application.dto.IncidentResponse;
import com.kyronic.riskengine.olts.application.dto.ReferenceDataOptionResponse;
import com.kyronic.riskengine.olts.application.mapper.IncidentMapperImpl;
import com.kyronic.riskengine.olts.application.service.AuthReferenceDataGateway;
import com.kyronic.riskengine.olts.application.service.OltsIncidentService;
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

    private static final UUID OPERATIONS_DEPARTMENT_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    private static final UUID HEAD_OFFICE_BRANCH_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

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
                code -> code,
                Clock.fixed(Instant.parse("2026-08-05T08:00:00Z"), ZoneOffset.UTC)
        );
        jwt = Jwt.withTokenValue("token")
                .subject("risk.inputter")
                .claim("userId", "11111111-1111-1111-1111-111111111111")
                .header("alg", "HS256")
                .build();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        controller = new OltsIncidentController(service, new IncidentMapperImpl(), new StubAuthReferenceDataGateway());
    }

    @Test
    void createsIncident() {
        CreateIncidentRequest request = new CreateIncidentRequest(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                HEAD_OFFICE_BRANCH_ID,
                OPERATIONS_DEPARTMENT_ID,
                "INTERNAL_FRAUD",
                "INCIDENT",
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
        assertThat(response.data().departmentName()).isEqualTo("Operations");
        assertThat(response.data().branchName()).isEqualTo("Head Office");
        assertThat(response.data().netLoss()).isEqualByComparingTo("90.00");
    }

    @Test
    void listsIncidents() {
        CreateIncidentRequest request = new CreateIncidentRequest(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                HEAD_OFFICE_BRANCH_ID,
                OPERATIONS_DEPARTMENT_ID,
                "INTERNAL_FRAUD",
                "INCIDENT",
                Severity.HIGH,
                "System outage detected by branch operations",
                "USD",
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00")
        );

        controller.create(request, jwt);

        ApiResponse<List<IncidentResponse>> response = controller.list(jwt);

        assertThat(response.success()).isTrue();
        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).departmentName()).isEqualTo("Operations");
        assertThat(response.data().get(0).branchName()).isEqualTo("Head Office");
    }

    @Test
    void rejectsInvalidCurrencyCodeAtRequestBoundary() {
        CreateIncidentRequest request = new CreateIncidentRequest(
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "INTERNAL_FRAUD",
                "INCIDENT",
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

    private static final class StubAuthReferenceDataGateway extends AuthReferenceDataGateway {

        private StubAuthReferenceDataGateway() {
            super(org.springframework.web.client.RestClient.builder());
        }

        @Override
        public List<ReferenceDataOptionResponse> listDepartments(String authorizationHeader) {
            return List.of(new ReferenceDataOptionResponse(
                    OPERATIONS_DEPARTMENT_ID,
                    "OPS",
                    "Operations",
                    true
            ));
        }

        @Override
        public List<ReferenceDataOptionResponse> listBranches(String authorizationHeader) {
            return List.of(new ReferenceDataOptionResponse(
                    HEAD_OFFICE_BRANCH_ID,
                    "HQ",
                    "Head Office",
                    true
            ));
        }
    }
}
