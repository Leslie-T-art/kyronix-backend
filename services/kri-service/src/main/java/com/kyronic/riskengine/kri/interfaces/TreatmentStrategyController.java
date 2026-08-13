package com.kyronic.riskengine.kri.interfaces;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.kri.application.dto.TreatmentStrategyRequest;
import com.kyronic.riskengine.kri.application.dto.TreatmentStrategyResponse;
import com.kyronic.riskengine.kri.application.service.TreatmentStrategyService;
import com.kyronic.riskengine.kri.infrastructure.configuration.KriOpenApiConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kri/treatment-strategies")
@Tag(name = "KRI Treatment Strategies", description = "CRUD endpoints for treatment strategies.")
@SecurityRequirement(name = KriOpenApiConfiguration.BEARER_SCHEME)
public class TreatmentStrategyController {

    private final TreatmentStrategyService service;

    public TreatmentStrategyController(TreatmentStrategyService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create treatment strategy", description = "Capture a new treatment strategy.")
    public ApiResponse<TreatmentStrategyResponse> create(@Valid @RequestBody TreatmentStrategyRequest request) {
        return ApiResponse.success("Treatment strategy created successfully", service.create(request), null);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List treatment strategies", description = "Fetch all treatment strategies.")
    public ApiResponse<List<TreatmentStrategyResponse>> list() {
        return ApiResponse.success("Treatment strategies retrieved successfully", service.list(), null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get treatment strategy", description = "Fetch one treatment strategy by id.")
    public ApiResponse<TreatmentStrategyResponse> get(@PathVariable("id") Long id) {
        return ApiResponse.success("Treatment strategy retrieved successfully", service.get(id), null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update treatment strategy", description = "Update a treatment strategy.")
    public ApiResponse<TreatmentStrategyResponse> update(@PathVariable("id") Long id,
                                                         @Valid @RequestBody TreatmentStrategyRequest request) {
        return ApiResponse.success("Treatment strategy updated successfully", service.update(id, request), null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete treatment strategy", description = "Delete a treatment strategy.")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ApiResponse.success("Treatment strategy deleted successfully", null, null);
    }
}
