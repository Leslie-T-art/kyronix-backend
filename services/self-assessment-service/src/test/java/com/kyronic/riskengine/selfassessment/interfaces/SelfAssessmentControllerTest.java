package com.kyronic.riskengine.selfassessment.interfaces;

import com.kyronic.riskengine.common.api.PageResponse;
import com.kyronic.riskengine.selfassessment.application.dto.SelfAssessmentRequest;
import com.kyronic.riskengine.selfassessment.application.dto.SelfAssessmentResponse;
import com.kyronic.riskengine.selfassessment.application.service.SelfAssessmentService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SelfAssessmentControllerTest {

    @Test
    void listWrapsPaginationResponse() {
        SelfAssessmentController controller = new SelfAssessmentController(new FixedService());

        var response = controller.list(0, 20, "createdAt", "desc");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isInstanceOf(PageResponse.class);
        assertThat(response.data().content()).hasSize(1);
    }

    @Test
    void getUsesIntegerId() {
        SelfAssessmentController controller = new SelfAssessmentController(new FixedService());

        var response = controller.get(1L);

        assertThat(response.data().id()).isEqualTo(1L);
        assertThat(response.data().rcsaId()).isEqualTo("RCSA-2026-000001");
    }

    private static final class FixedService extends SelfAssessmentService {
        private FixedService() {
            super(null, null, null, null);
        }

        @Override
        public org.springframework.data.domain.Page<SelfAssessmentResponse> list(int page, int size, String sortBy, String sortDirection) {
            return new PageImpl<>(List.of(response()));
        }

        @Override
        public SelfAssessmentResponse get(Long id) {
            return response();
        }

        private SelfAssessmentResponse response() {
            return new SelfAssessmentResponse(
                    1L,
                    "RCSA-2026-000001",
                    "2026-Q3",
                    4L,
                    "Payments",
                    "RISK-17",
                    "Scenario",
                    "Cause",
                    "Impact",
                    3,
                    4,
                    12,
                    "MEDIUM",
                    Set.of("CTRL-1"),
                    "Adequate",
                    "Adequate",
                    "Adequate",
                    2,
                    3,
                    6,
                    "LOW",
                    "MITIGATE",
                    true,
                    "Enhance maker-checker",
                    Set.of("KRI-1"),
                    Set.of("OLTS-1"),
                    Set.of("ISSUE-1"),
                    "PENDING",
                    "VERIFIED",
                    "Comment",
                    LocalDate.of(2026, 8, 1),
                    LocalDate.of(2026, 9, 1),
                    Instant.parse("2026-08-10T09:00:00Z"),
                    "system.admin",
                    Instant.parse("2026-08-10T09:00:00Z"),
                    "system.admin"
            );
        }
    }
}
