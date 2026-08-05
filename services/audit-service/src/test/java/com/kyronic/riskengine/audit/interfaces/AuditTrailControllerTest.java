package com.kyronic.riskengine.audit.interfaces;

import com.kyronic.riskengine.audit.application.dto.AuditTrailResponse;
import com.kyronic.riskengine.audit.application.service.AuditTrailService;
import com.kyronic.riskengine.common.observability.AuditTrailEntryRequest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuditTrailControllerTest {

    @Test
    void returnsPlatformAuditTrailWithFilters() {
        AuditTrailController controller = new AuditTrailController(new FixedAuditTrailService());
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();

        var response = controller.list(request, null, "olts-service", "risk.inputter", "SUCCESS", "/api/v1/olts", "incidents", 25);

        assertThat(response.success()).isTrue();
        assertThat(response.message()).isEqualTo("Audit trail retrieved successfully");
        assertThat(response.data()).hasSize(1);
        assertThat(response.data().get(0).serviceName()).isEqualTo("olts-service");
        assertThat(response.correlationId()).isNotBlank();
    }

    private static final class FixedAuditTrailService extends AuditTrailService {
        private FixedAuditTrailService() {
            super(null);
        }

        @Override
        public List<AuditTrailResponse> listTrail(String serviceName,
                                                  String username,
                                                  String outcome,
                                                  String requestPath,
                                                  String searchText,
                                                  Integer limit) {
            return List.of(new AuditTrailResponse(
                    UUID.randomUUID(),
                    serviceName,
                    "HTTP_REQUEST",
                    "GET /api/v1/olts/incidents",
                    "GET",
                    requestPath,
                    null,
                    200,
                    outcome,
                    username,
                    "11111111-1111-1111-1111-111111111111",
                    "127.0.0.1",
                    "JUnit",
                    "corr-123",
                    Instant.parse("2026-08-05T08:30:00Z")
            ));
        }

        @Override
        public AuditTrailResponse record(AuditTrailEntryRequest request) {
            throw new UnsupportedOperationException("not used");
        }
    }
}
