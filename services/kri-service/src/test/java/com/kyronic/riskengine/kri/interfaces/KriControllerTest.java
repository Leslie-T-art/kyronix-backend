package com.kyronic.riskengine.kri.interfaces;

import com.kyronic.riskengine.kri.application.dto.KriRequest;
import com.kyronic.riskengine.kri.application.dto.KriResponse;
import com.kyronic.riskengine.kri.application.service.KriService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class KriControllerTest {

    @Test
    void createReturnsApiResponse() {
        KriService service = new FixedKriService();
        KriController controller = new KriController(service);

        var apiResponse = controller.create(request());

        assertThat(apiResponse.success()).isTrue();
        assertThat(apiResponse.message()).isEqualTo("KRI created successfully");
        assertThat(apiResponse.data().kriId()).isEqualTo("KRI-2026-00001");
    }

    @Test
    void listReturnsAllKris() {
        KriService service = new FixedKriService();
        KriController controller = new KriController(service);

        var apiResponse = controller.list();

        assertThat(apiResponse.success()).isTrue();
        assertThat(apiResponse.data()).hasSize(1);
    }

    @Test
    void updateReturnsUpdatedKri() {
        KriService service = new FixedKriService();
        KriController controller = new KriController(service);

        var apiResponse = controller.update("KRI-2026-00001", request());

        assertThat(apiResponse.success()).isTrue();
        assertThat(apiResponse.message()).isEqualTo("KRI updated successfully");
    }

    private KriRequest request() {
        return new KriRequest(
                "Loan Default Ratio",
                "Credit",
                "Head of Credit",
                "Retail Banking",
                "MONTHLY",
                "Tracks default exposure for the retail loan book.",
                "PERCENTAGE",
                new BigDecimal("5.0000"),
                "LOWER_IS_BETTER",
                new BigDecimal("4.0000"),
                new BigDecimal("6.0000"),
                new BigDecimal("8.0000"),
                new BigDecimal("5.5000"),
                "Core Banking",
                LocalDate.of(2026, 8, 30),
                "RISK-001",
                "Risk Committee",
                "Current value exceeds red threshold"
        );
    }

    private KriResponse response() {
        return new KriResponse(
                UUID.randomUUID(),
                "KRI-2026-00001",
                "Loan Default Ratio",
                "Credit",
                "Head of Credit",
                "Retail Banking",
                "MONTHLY",
                "Tracks default exposure for the retail loan book.",
                "PERCENTAGE",
                new BigDecimal("5.0000"),
                "LOWER_IS_BETTER",
                new BigDecimal("4.0000"),
                new BigDecimal("6.0000"),
                new BigDecimal("8.0000"),
                new BigDecimal("5.5000"),
                "Core Banking",
                LocalDate.of(2026, 8, 30),
                "RISK-001",
                "Risk Committee",
                "Current value exceeds red threshold",
                Instant.parse("2026-08-06T08:30:00Z"),
                "risk.inputter",
                Instant.parse("2026-08-06T08:30:00Z"),
                "risk.inputter"
        );
    }

    private final class FixedKriService extends KriService {
        private FixedKriService() {
            super(null, null, null, null);
        }

        @Override
        public KriResponse create(KriRequest request) {
            return response();
        }

        @Override
        public List<KriResponse> list() {
            return List.of(response());
        }

        @Override
        public KriResponse update(String kriId, KriRequest request) {
            return response();
        }
    }
}
