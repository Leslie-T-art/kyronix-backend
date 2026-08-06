package com.kyronic.riskengine.olts.interfaces.rest;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.LossCategoryRequest;
import com.kyronic.riskengine.olts.application.dto.LossCategoryResponse;
import com.kyronic.riskengine.olts.application.service.LossCategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LossCategoryControllerTest {

    private LossCategoryController controller;

    @BeforeEach
    void setUp() {
        controller = new LossCategoryController(new FakeLossCategoryService());
    }

    @Test
    void supportsFullCrud() {
        UUID id = UUID.fromString("11111111-1111-1111-1111-111111111111");

        ApiResponse<LossCategoryResponse> created = controller.create(
                new LossCategoryRequest("INT-FRD", "Internal Fraud", "Internal fraud losses"));
        ApiResponse<List<LossCategoryResponse>> listed = controller.list();
        ApiResponse<LossCategoryResponse> fetched = controller.get(id);
        ApiResponse<LossCategoryResponse> updated = controller.update(
                id,
                new LossCategoryRequest("INT-FRD-1", "Internal Fraud Updated", "Updated description"));
        ApiResponse<Void> deleted = controller.delete(id);

        assertThat(created.success()).isTrue();
        assertThat(created.data().code()).isEqualTo("INT-FRD");
        assertThat(listed.data()).hasSize(1);
        assertThat(fetched.data().name()).isEqualTo("Internal Fraud");
        assertThat(updated.data().code()).isEqualTo("INT-FRD-1");
        assertThat(deleted.success()).isTrue();
    }

    private static final class FakeLossCategoryService extends LossCategoryService {
        private FakeLossCategoryService() {
            super(null);
        }

        @Override
        public LossCategoryResponse create(LossCategoryRequest request) {
            return new LossCategoryResponse(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    request.code(),
                    request.name(),
                    request.description()
            );
        }

        @Override
        public List<LossCategoryResponse> list() {
            return List.of(new LossCategoryResponse(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "INT-FRD",
                    "Internal Fraud",
                    "Internal fraud losses"
            ));
        }

        @Override
        public LossCategoryResponse get(UUID id) {
            return new LossCategoryResponse(id, "INT-FRD", "Internal Fraud", "Internal fraud losses");
        }

        @Override
        public LossCategoryResponse update(UUID id, LossCategoryRequest request) {
            return new LossCategoryResponse(id, request.code(), request.name(), request.description());
        }

        @Override
        public void delete(UUID id) {
        }
    }
}
