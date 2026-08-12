package com.kyronic.riskengine.olts.interfaces.rest.config;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.OltsConfigurationRequest;
import com.kyronic.riskengine.olts.application.dto.OltsConfigurationResponse;
import com.kyronic.riskengine.olts.application.service.config.AbstractOltsConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.UUID;

public abstract class AbstractOltsConfigurationController {

    private final AbstractOltsConfigurationService<?> service;
    private final String label;

    protected AbstractOltsConfigurationController(AbstractOltsConfigurationService<?> service, String label) {
        this.service = service;
        this.label = label;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create configuration item")
    public ApiResponse<OltsConfigurationResponse> create(@Valid @RequestBody OltsConfigurationRequest request,
                                                         @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.success(label + " created successfully",
                service.create(request, actorUserId(jwt)),
                correlationId());
    }

    @GetMapping
    @Operation(summary = "List configuration items")
    public ApiResponse<List<OltsConfigurationResponse>> list() {
        return ApiResponse.success(label + " retrieved successfully", service.list(), correlationId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get configuration item")
    public ApiResponse<OltsConfigurationResponse> get(@PathVariable("id") Long id) {
        return ApiResponse.success(label + " retrieved successfully", service.get(id), correlationId());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update configuration item")
    public ApiResponse<OltsConfigurationResponse> update(@PathVariable("id") Long id,
                                                         @Valid @RequestBody OltsConfigurationRequest request,
                                                         @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.success(label + " updated successfully",
                service.update(id, request, actorUserId(jwt)),
                correlationId());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete configuration item")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ApiResponse.success(label + " deleted successfully", null, correlationId());
    }

    private Long actorUserId(Jwt jwt) {
        if (jwt == null) {
            throw new IllegalArgumentException("authenticated user is required");
        }
        return Long.valueOf(jwt.getClaimAsString("userId"));
    }

    private String correlationId() {
        return UUID.randomUUID().toString();
    }
}
