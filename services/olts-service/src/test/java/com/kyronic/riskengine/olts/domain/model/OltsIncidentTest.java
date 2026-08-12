package com.kyronic.riskengine.olts.domain.model;

import com.kyronic.riskengine.common.authorization.AuthorizationException;
import com.kyronic.riskengine.common.authorization.AuthorizationStatus;
import com.kyronic.riskengine.common.authorization.SegregationOfDutiesPolicy;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OltsIncidentTest {

    @Test
    void calculatesNetLossFromGrossLossAndRemediationCost() {
        assertThat(OltsIncident.calculateNetLoss(new BigDecimal("100.00"), new BigDecimal("15.25")))
                .isEqualByComparingTo("115.25");
    }

    @Test
    void makerCannotAuthorizeOwnIncident() {
        Long maker = 1001L;
        OltsIncident incident = incident(maker, 101L);
        incident.submit(maker, Instant.parse("2026-08-05T08:00:00Z"));
        incident.beginAuthorizationReview(1002L, Instant.parse("2026-08-05T08:01:00Z"));

        assertThatThrownBy(() -> incident.authorize(maker, Instant.parse("2026-08-05T08:02:00Z"), new SegregationOfDutiesPolicy()))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void lastEditorCannotAuthorizeOwnIncident() {
        Long maker = 1001L;
        Long reviewer = 1002L;
        OltsIncident incident = incident(maker, 101L);
        incident.submit(maker, Instant.parse("2026-08-05T08:00:00Z"));
        incident.beginAuthorizationReview(reviewer, Instant.parse("2026-08-05T08:01:00Z"));

        assertThatThrownBy(() -> incident.authorize(reviewer, Instant.parse("2026-08-05T08:02:00Z"), new SegregationOfDutiesPolicy()))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void validAuthorizerCanAuthorizeIncident() {
        Long maker = 1001L;
        OltsIncident incident = incident(maker, 101L);
        incident.submit(maker, Instant.parse("2026-08-05T08:00:00Z"));
        incident.beginAuthorizationReview(1002L, Instant.parse("2026-08-05T08:01:00Z"));

        Long authorizer = 1003L;
        incident.authorize(authorizer, Instant.parse("2026-08-05T08:02:00Z"), new SegregationOfDutiesPolicy());

        assertThat(incident.getAuthorizationStatus()).isEqualTo(AuthorizationStatus.AUTHORIZED);
        assertThat(incident.getAuthorizedBy()).isEqualTo(authorizer);
    }

    private OltsIncident incident(Long maker, Long departmentId) {
        return OltsIncident.create(
                "OLTS-2026-00001",
                maker,
                "risk.inputter",
                11L,
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 3),
                LocalDate.of(2026, 8, 2),
                departmentId,
                201L,
                "Cash Discrepancy",
                "Finance",
                "Teller Services",
                21L,
                "Cash discrepancy detected during end of day reconciliation",
                "Cash box sealed",
                31L,
                "Human error",
                41L,
                true,
                51L,
                new BigDecimal("100.00"),
                new BigDecimal("10.00"),
                61L,
                "GL-001",
                71L,
                "Customer impact",
                "Short delay at branch",
                Severity.HIGH,
                "Reconcile and retrain",
                "Responsible User",
                LocalDate.of(2026, 8, 10),
                81L,
                true,
                "evidence.pdf",
                LocalDate.of(2026, 8, 12),
                "Pending closure",
                Instant.parse("2026-08-05T07:59:00Z")
        );
    }
}
