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

import static org.assertj.core.api.Assertions.assertThat;

class OltsIncidentControllerTest {

    private static final Long OPERATIONS_DEPARTMENT_ID = 101L;
    private static final Long HEAD_OFFICE_BRANCH_ID = 201L;

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
                (departmentId, permission) -> List.of(new AuthorizerCandidate(1002L, departmentId, Set.of(permission), true, false, null, null, null)),
                event -> {
                },
                code -> code,
                Clock.fixed(Instant.parse("2026-08-05T08:00:00Z"), ZoneOffset.UTC)
        );
        jwt = Jwt.withTokenValue("token")
                .subject("risk.inputter")
                .claim("userId", "1001")
                .header("alg", "HS256")
                .build();
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        controller = new OltsIncidentController(service, new IncidentMapperImpl(), new StubAuthReferenceDataGateway());
    }

    @Test
    void createsIncident() {
        CreateIncidentRequest request = new CreateIncidentRequest(
                "System Outage",
                11L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                LocalDate.of(2026, 8, 2),
                HEAD_OFFICE_BRANCH_ID,
                OPERATIONS_DEPARTMENT_ID,
                "Operations",
                "Switching",
                21L,
                "System outage detected by branch operations",
                "Failover executed",
                31L,
                "Infrastructure instability",
                41L,
                true,
                51L,
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                61L,
                "GL-100",
                71L,
                "Service outage",
                "Branch operations impacted",
                Severity.HIGH,
                "Replace switch",
                "risk.inputter",
                LocalDate.of(2026, 8, 15),
                81L,
                true,
                "evidence.docx",
                LocalDate.of(2026, 8, 20),
                "Awaiting validation"
        );

        ApiResponse<IncidentResponse> response = controller.create(request, jwt);

        assertThat(response.success()).isTrue();
        assertThat(response.data().eventId()).isEqualTo("OLTS-2026-00001");
        assertThat(response.data().reportedBy()).isEqualTo("risk.inputter");
        assertThat(response.data().eventOwner()).isEqualTo("risk.inputter");
        assertThat(response.data().departmentName()).isEqualTo("Operations");
        assertThat(response.data().branchName()).isEqualTo("Head Office");
        assertThat(response.data().netLoss()).isEqualByComparingTo("110.00");
    }

    @Test
    void listsIncidents() {
        CreateIncidentRequest request = new CreateIncidentRequest(
                "System Outage",
                11L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                LocalDate.of(2026, 8, 2),
                HEAD_OFFICE_BRANCH_ID,
                OPERATIONS_DEPARTMENT_ID,
                "Operations",
                "Switching",
                21L,
                "System outage detected by branch operations",
                "Failover executed",
                31L,
                "Infrastructure instability",
                41L,
                true,
                51L,
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                61L,
                "GL-100",
                71L,
                "Service outage",
                "Branch operations impacted",
                Severity.HIGH,
                "Replace switch",
                "risk.inputter",
                LocalDate.of(2026, 8, 15),
                81L,
                true,
                "evidence.docx",
                LocalDate.of(2026, 8, 20),
                "Awaiting validation"
        );

        controller.create(request, jwt);

        ApiResponse<List<IncidentResponse>> response = controller.list(jwt);

        assertThat(response.success()).isTrue();
        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).departmentName()).isEqualTo("Operations");
        assertThat(response.data().get(0).branchName()).isEqualTo("Head Office");
    }

    @Test
    void rejectsBlankEventTitleAtRequestBoundary() {
        CreateIncidentRequest request = new CreateIncidentRequest(
                " ",
                11L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                LocalDate.of(2026, 8, 2),
                999L,
                888L,
                "Operations",
                "Switching",
                21L,
                "System outage detected by branch operations",
                "Failover executed",
                31L,
                "Infrastructure instability",
                41L,
                true,
                51L,
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                61L,
                "GL-100",
                71L,
                "Service outage",
                "Branch operations impacted",
                Severity.HIGH,
                "Replace switch",
                "risk.inputter",
                LocalDate.of(2026, 8, 15),
                81L,
                true,
                "evidence.docx",
                LocalDate.of(2026, 8, 20),
                "Awaiting validation"
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
        public List<com.kyronic.riskengine.olts.domain.model.OltsIncident> findAll() {
            return incident == null ? List.of() : List.of(incident);
        }

        @Override
        public void delete(com.kyronic.riskengine.olts.domain.model.OltsIncident incident) {
            this.incident = null;
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
