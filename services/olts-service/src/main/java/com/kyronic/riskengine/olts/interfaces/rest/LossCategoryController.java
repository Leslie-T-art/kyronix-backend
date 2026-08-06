package com.kyronic.riskengine.olts.interfaces.rest;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.LossCategoryRequest;
import com.kyronic.riskengine.olts.application.dto.LossCategoryResponse;
import com.kyronic.riskengine.olts.application.service.LossCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/olts/loss-categories")
@Tag(name = "OLTS Loss Categories", description = "CRUD endpoints for OLTS loss category configuration.")
public class LossCategoryController {

    private final LossCategoryService service;

    public LossCategoryController(LossCategoryService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create loss category", description = "Create a new OLTS loss category.")
    public ApiResponse<LossCategoryResponse> create(@Valid @RequestBody LossCategoryRequest request) {
        String correlationId = correlationId();
        return ApiResponse.success("Loss category created successfully", service.create(request), correlationId);
    }

    @GetMapping
    @Operation(summary = "List loss categories", description = "Fetch all OLTS loss categories.")
    public ApiResponse<List<LossCategoryResponse>> list() {
        String correlationId = correlationId();
        return ApiResponse.success("Loss categories retrieved successfully", service.list(), correlationId);
    }

    @GetMapping("/{lossCategoryId}")
    @Operation(summary = "Get loss category", description = "Fetch one OLTS loss category by id.")
    public ApiResponse<LossCategoryResponse> get(@PathVariable("lossCategoryId") UUID lossCategoryId) {
        String correlationId = correlationId();
        return ApiResponse.success("Loss category retrieved successfully", service.get(lossCategoryId), correlationId);
    }

    @PutMapping("/{lossCategoryId}")
    @Operation(summary = "Update loss category", description = "Update an OLTS loss category.")
    public ApiResponse<LossCategoryResponse> update(@PathVariable("lossCategoryId") UUID lossCategoryId,
                                                    @Valid @RequestBody LossCategoryRequest request) {
        String correlationId = correlationId();
        return ApiResponse.success("Loss category updated successfully", service.update(lossCategoryId, request), correlationId);
    }

    @DeleteMapping("/{lossCategoryId}")
    @Operation(summary = "Delete loss category", description = "Delete an OLTS loss category.")
    public ApiResponse<Void> delete(@PathVariable("lossCategoryId") UUID lossCategoryId) {
        String correlationId = correlationId();
        service.delete(lossCategoryId);
        return ApiResponse.success("Loss category deleted successfully", null, correlationId);
    }

    private String correlationId() {
        return UUID.randomUUID().toString();
    }
}
