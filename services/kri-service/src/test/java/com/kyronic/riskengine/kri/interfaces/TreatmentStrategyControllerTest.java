package com.kyronic.riskengine.kri.interfaces;

import com.kyronic.riskengine.kri.application.dto.TreatmentStrategyRequest;
import com.kyronic.riskengine.kri.application.dto.TreatmentStrategyResponse;
import com.kyronic.riskengine.kri.application.service.TreatmentStrategyService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TreatmentStrategyControllerTest {

    @Test
    void createReturnsApiResponse() {
        TreatmentStrategyController controller = new TreatmentStrategyController(new FixedTreatmentStrategyService());

        var apiResponse = controller.create(new TreatmentStrategyRequest("MITIGATE", "Mitigate", "ACTIVE"));

        assertThat(apiResponse.success()).isTrue();
        assertThat(apiResponse.message()).isEqualTo("Treatment strategy created successfully");
        assertThat(apiResponse.data().code()).isEqualTo("MITIGATE");
    }

    @Test
    void listReturnsAllStrategies() {
        TreatmentStrategyController controller = new TreatmentStrategyController(new FixedTreatmentStrategyService());

        var apiResponse = controller.list();

        assertThat(apiResponse.success()).isTrue();
        assertThat(apiResponse.data()).hasSize(1);
    }

    private static final class FixedTreatmentStrategyService extends TreatmentStrategyService {
        private FixedTreatmentStrategyService() {
            super(null, null, null);
        }

        @Override
        public TreatmentStrategyResponse create(TreatmentStrategyRequest request) {
            return response();
        }

        @Override
        public List<TreatmentStrategyResponse> list() {
            return List.of(response());
        }
    }

    private static TreatmentStrategyResponse response() {
        return new TreatmentStrategyResponse(
                1L,
                "MITIGATE",
                "Mitigate",
                "ACTIVE",
                Instant.parse("2026-08-13T10:00:00Z"),
                "risk.inputter",
                Instant.parse("2026-08-13T10:00:00Z"),
                "risk.inputter"
        );
    }
}
