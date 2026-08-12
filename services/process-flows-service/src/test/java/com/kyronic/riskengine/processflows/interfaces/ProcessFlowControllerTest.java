package com.kyronic.riskengine.processflows.interfaces;

import com.kyronic.riskengine.common.api.PageResponse;
import com.kyronic.riskengine.processflows.application.dto.ProcessFlowResponse;
import com.kyronic.riskengine.processflows.application.service.ProcessFlowService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessFlowControllerTest {

    @Test
    void listWrapsPageResponse() {
        ProcessFlowController controller = new ProcessFlowController(new FixedService());

        var response = controller.list(0, 20, "createdAt", "desc");

        assertThat(response.success()).isTrue();
        assertThat(response.data()).isInstanceOf(PageResponse.class);
        assertThat(response.data().content()).hasSize(1);
    }

    private static final class FixedService extends ProcessFlowService {
        private FixedService() {
            super(null, null, null, null);
        }

        @Override
        public org.springframework.data.domain.Page<ProcessFlowResponse> list(int page, int size, String sortBy, String sortDirection) {
            return new PageImpl<>(List.of(new ProcessFlowResponse(
                    1L,
                    "PF-2026-000001",
                    "Card Disputes",
                    4L,
                    "Head of Operations",
                    "ACTIVE",
                    "Card dispute escalation flow",
                    Instant.parse("2026-08-10T09:00:00Z"),
                    "system.admin",
                    Instant.parse("2026-08-10T09:00:00Z"),
                    "system.admin"
            )));
        }
    }
}
