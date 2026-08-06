package com.kyronic.riskengine.auth.interfaces;

import com.kyronic.riskengine.auth.application.dto.LoginRequest;
import com.kyronic.riskengine.auth.application.dto.LoginResponse;
import com.kyronic.riskengine.auth.application.dto.AuditEventCommand;
import com.kyronic.riskengine.auth.application.dto.AuditEventResponse;
import com.kyronic.riskengine.auth.application.dto.AuthMeResponse;
import com.kyronic.riskengine.auth.application.service.AdministrationService;
import com.kyronic.riskengine.auth.application.service.AuditRequestFactory;
import com.kyronic.riskengine.auth.application.service.AuditTrailService;
import com.kyronic.riskengine.auth.application.service.AuthTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest {

    @Test
    void loginReturnsBearerTokenPayload() {
        RecordingAuditTrailService auditTrailService = new RecordingAuditTrailService();
        AuthController controller = new AuthController(new FakeAuthTokenService(), new FakeAdministrationService(), auditTrailService, requestFactory());
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        request.setAttribute(AuditRequestFactory.CORRELATION_ID_ATTRIBUTE, "corr-1");

        var response = controller.login(new LoginRequest("risk.inputter", "ChangeMe123!"), request);

        assertThat(response.success()).isTrue();
        assertThat(response.data().accessToken()).isEqualTo("jwt-token");
        assertThat(response.data().tokenType()).isEqualTo("Bearer");
        assertThat(response.correlationId()).isEqualTo("corr-1");
        assertThat(auditTrailService.recordedEventType).isEqualTo("AUTH_LOGIN_SUCCESS");
        assertThat(auditTrailService.recordedUsername).isEqualTo("risk.inputter");
        assertThat(auditTrailService.recordedNewValues).doesNotContain("jwt-token");
    }

    @Test
    void meReturnsCapturedUserProfile() {
        RecordingAuditTrailService auditTrailService = new RecordingAuditTrailService();
        AuthController controller = new AuthController(new FakeAuthTokenService(), new FakeAdministrationService(), auditTrailService, requestFactory());
        var authentication = new UsernamePasswordAuthenticationToken(
                "risk.inputter",
                "n/a",
                Set.of(new SimpleGrantedAuthority("ROLE_INPUTTER"), new SimpleGrantedAuthority("OLTS_CREATE"))
        );
        org.springframework.mock.web.MockHttpServletRequest request = new org.springframework.mock.web.MockHttpServletRequest();
        request.setAttribute(AuditRequestFactory.CORRELATION_ID_ATTRIBUTE, "corr-2");

        var response = controller.me(authentication, request);

        assertThat(response.success()).isTrue();
        assertThat(response.data().username()).isEqualTo("risk.inputter");
        assertThat(response.data().fullName()).isEqualTo("Risk Inputter");
        assertThat(response.data().active()).isTrue();
        assertThat(response.data().locked()).isFalse();
        assertThat(response.data().department()).isEqualTo(new AuthMeResponse.ReferenceAssignment(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "COM",
                "Compliance"
        ));
        assertThat(response.data().branch()).isEqualTo(new AuthMeResponse.ReferenceAssignment(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "HQ",
                "Head Office"
        ));
        assertThat(response.data().roles()).containsExactlyInAnyOrder(
                new AuthMeResponse.RoleAssignment("DEPARTMENT_HEAD", "Department Head"),
                new AuthMeResponse.RoleAssignment("INPUTTER", "Inputter")
        );
        assertThat(response.data().permissions()).containsExactlyInAnyOrder("OLTS_CREATE", "OLTS_READ");
        assertThat(response.correlationId()).isEqualTo("corr-2");
        assertThat(auditTrailService.recordedEventType).isEqualTo("AUTH_PROFILE_VIEWED");
    }

    private AuditRequestFactory requestFactory() {
        return new AuditRequestFactory(
                com.fasterxml.jackson.databind.json.JsonMapper.builder()
                        .findAndAddModules()
                        .build(),
                Clock.fixed(Instant.parse("2026-08-05T08:30:00Z"), ZoneOffset.UTC));
    }

    private static final class FakeAdministrationService extends AdministrationService {
        private FakeAdministrationService() {
            super(null, null, null, null);
        }

        @Override
        public AuthMeResponse getCurrentUserProfile(String username) {
            return new AuthMeResponse(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    username,
                    "Risk Inputter",
                    true,
                    false,
                    new AuthMeResponse.ReferenceAssignment(
                            UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                            "COM",
                            "Compliance"
                    ),
                    new AuthMeResponse.ReferenceAssignment(
                            UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                            "HQ",
                            "Head Office"
                    ),
                    Set.of(
                            new AuthMeResponse.RoleAssignment("DEPARTMENT_HEAD", "Department Head"),
                            new AuthMeResponse.RoleAssignment("INPUTTER", "Inputter")
                    ),
                    Set.of("OLTS_CREATE", "OLTS_READ")
            );
        }
    }

    private static final class FakeAuthTokenService extends AuthTokenService {
        private FakeAuthTokenService() {
            super(null, null, null, Clock.fixed(Instant.parse("2026-08-05T08:30:00Z"), ZoneOffset.UTC));
        }

        @Override
        public LoginResponse login(LoginRequest request) {
            return new LoginResponse(
                    "jwt-token",
                    "Bearer",
                    600,
                    Instant.parse("2026-08-05T08:30:00Z"),
                    Instant.parse("2026-08-05T08:40:00Z"),
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    request.username(),
                    "Risk Inputter",
                    UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                    UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                    Set.of("INPUTTER"),
                    Set.of("OLTS_CREATE", "OLTS_READ")
            );
        }
    }

    private static final class RecordingAuditTrailService extends AuditTrailService {
        private String recordedEventType;
        private String recordedUsername;
        private String recordedNewValues;

        private RecordingAuditTrailService() {
            super(null);
        }

        @Override
        public AuditEventResponse record(AuditEventCommand command) {
            this.recordedEventType = command.eventType();
            this.recordedUsername = command.username();
            this.recordedNewValues = command.newValues();
            return new AuditEventResponse(
                    UUID.randomUUID(),
                    command.eventType(),
                    command.action(),
                    "auth-service",
                    command.entityType(),
                    command.entityId(),
                    command.businessReference(),
                    command.userId(),
                    command.username(),
                    command.roles(),
                    command.permissions(),
                    command.result(),
                    command.failureReason(),
                    command.requestMethod(),
                    command.requestPath(),
                    command.sourceIp(),
                    command.userAgent(),
                    command.correlationId(),
                    command.oldValues(),
                    command.newValues(),
                    command.occurredAt()
            );
        }
    }
}
