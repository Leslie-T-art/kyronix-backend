package com.kyronic.riskengine.olts.domain.model;

import com.kyronic.riskengine.common.authorization.AuthorizationException;
import com.kyronic.riskengine.common.authorization.AuthorizationStatus;
import com.kyronic.riskengine.common.authorization.SegregationOfDutiesPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OltsIncidentTest {

    @Test
    void calculatesNetLossFromGrossLossAndRecoveries() {
        assertThat(OltsIncident.calculateNetLoss(new BigDecimal("100.00"), new BigDecimal("15.25")))
                .isEqualByComparingTo("84.75");
    }

    @Test
    void makerCannotAuthorizeOwnIncident() {
        UUID maker = UUID.randomUUID();
        OltsIncident incident = incident(maker, UUID.randomUUID());
        incident.submit(maker, Instant.parse("2026-08-05T08:00:00Z"));
        incident.beginAuthorizationReview(UUID.randomUUID(), Instant.parse("2026-08-05T08:01:00Z"));

        assertThatThrownBy(() -> incident.authorize(maker, Instant.parse("2026-08-05T08:02:00Z"), new SegregationOfDutiesPolicy()))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void lastEditorCannotAuthorizeOwnIncident() {
        UUID maker = UUID.randomUUID();
        UUID reviewer = UUID.randomUUID();
        OltsIncident incident = incident(maker, UUID.randomUUID());
        incident.submit(maker, Instant.parse("2026-08-05T08:00:00Z"));
        incident.beginAuthorizationReview(reviewer, Instant.parse("2026-08-05T08:01:00Z"));

        assertThatThrownBy(() -> incident.authorize(reviewer, Instant.parse("2026-08-05T08:02:00Z"), new SegregationOfDutiesPolicy()))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void validAuthorizerCanAuthorizeIncident() {
        UUID maker = UUID.randomUUID();
        OltsIncident incident = incident(maker, UUID.randomUUID());
        incident.submit(maker, Instant.parse("2026-08-05T08:00:00Z"));
        incident.beginAuthorizationReview(UUID.randomUUID(), Instant.parse("2026-08-05T08:01:00Z"));

        UUID authorizer = UUID.randomUUID();
        incident.authorize(authorizer, Instant.parse("2026-08-05T08:02:00Z"), new SegregationOfDutiesPolicy());

        assertThat(incident.getAuthorizationStatus()).isEqualTo(AuthorizationStatus.AUTHORIZED);
        assertThat(incident.getAuthorizedBy()).isEqualTo(authorizer);
    }

    private OltsIncident incident(UUID maker, UUID departmentId) {
        return OltsIncident.create(
                "OLTS-2026-00001",
                departmentId,
                UUID.randomUUID(),
                maker,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 2),
                LossCategory.INTERNAL_FRAUD,
                EventType.INCIDENT,
                Severity.HIGH,
                "Cash discrepancy detected during end of day reconciliation",
                "USD",
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                new BigDecimal("25.00"),
                UUID.randomUUID(),
                "Responsible User",
                Instant.parse("2026-08-05T07:59:00Z")
        );
    }
}
