package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.AuditEventCommand;
import com.kyronic.riskengine.auth.application.dto.AuditEventResponse;
import com.kyronic.riskengine.auth.application.service.AuditRequestFactory;
import com.kyronic.riskengine.auth.application.service.AuditTrailService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuditTrailControllerTest {

    @Test
    void listsAuditEventsUsingRequestCorrelationId() {
        FixedAuditTrailService service = new FixedAuditTrailService();
        AuditRequestFactory requestFactory = new AuditRequestFactory(
                com.fasterxml.jackson.databind.json.JsonMapper.builder()
                        .findAndAddModules()
                        .build(),
                Clock.fixed(Instant.parse("2026-08-05T08:30:00Z"), ZoneOffset.UTC));
        AuditTrailController controller = new AuditTrailController(service, requestFactory);
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        request.setAttribute(AuditRequestFactory.CORRELATION_ID_ATTRIBUTE, "corr-123");

        var response = controller.listAuditEvents(request);

        assertThat(response.success()).isTrue();
        assertThat(response.correlationId()).isEqualTo("corr-123");
        assertThat(response.data()).hasSize(1);
    }

    private static final class FixedAuditTrailService extends AuditTrailService {
        private FixedAuditTrailService() {
            super(null);
        }

        @Override
        public List<AuditEventResponse> findAuditEvents() {
            return List.of(new AuditEventResponse(
                    1L,
                    "AUTH_LOGIN_SUCCESS",
                    "LOGIN",
                    "auth-service",
                    "AUTH_SESSION",
                    null,
                    null,
                    111111L,
                    "risk.inputter",
                    "INPUTTER",
                    "OLTS_CREATE",
                    "SUCCESS",
                    null,
                    "POST",
                    "/api/v1/auth/login",
                    "127.0.0.1",
                    "JUnit",
                    "corr-123",
                    null,
                    "{\"username\":\"risk.inputter\"}",
                    Instant.parse("2026-08-05T08:30:00Z")
            ));
        }

        @Override
        public AuditEventResponse record(AuditEventCommand command) {
            throw new UnsupportedOperationException("not used");
        }
    }
}
