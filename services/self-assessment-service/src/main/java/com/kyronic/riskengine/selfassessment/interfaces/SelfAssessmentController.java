package com.kyronic.riskengine.selfassessment.interfaces;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.common.api.PageResponse;
import com.kyronic.riskengine.selfassessment.application.dto.SelfAssessmentRequest;
import com.kyronic.riskengine.selfassessment.application.dto.SelfAssessmentResponse;
import com.kyronic.riskengine.selfassessment.application.service.SelfAssessmentService;
import com.kyronic.riskengine.selfassessment.infrastructure.configuration.SelfAssessmentOpenApiConfiguration;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/self-assessments")
@Tag(name = "Self Assessments", description = "CRUD endpoints for RCSA / self assessment records.")
@SecurityRequirement(name = SelfAssessmentOpenApiConfiguration.BEARER_SCHEME)
public class SelfAssessmentController {

    private final SelfAssessmentService service;

    public SelfAssessmentController(SelfAssessmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create self assessment")
    public ApiResponse<SelfAssessmentResponse> create(@Valid @RequestBody SelfAssessmentRequest request) {
        return ApiResponse.success("Self assessment created successfully", service.create(request), null);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List self assessments")
    public ApiResponse<PageResponse<SelfAssessmentResponse>> list(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                  @RequestParam(name = "size", defaultValue = "20") int size,
                                                                  @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                                  @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection) {
        return ApiResponse.success(
                "Self assessments retrieved successfully",
                PageResponse.from(service.list(page, size, sortBy, sortDirection)),
                null
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get self assessment")
    public ApiResponse<SelfAssessmentResponse> get(@PathVariable("id") Long id) {
        return ApiResponse.success("Self assessment retrieved successfully", service.get(id), null);
    }

    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Count self assessments")
    public ApiResponse<Long> count(@RequestParam(name = "departmentId", required = false) Long departmentId) {
        return ApiResponse.success("Self assessment count retrieved successfully", service.count(departmentId), null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update self assessment")
    public ApiResponse<SelfAssessmentResponse> update(@PathVariable("id") Long id,
                                                      @Valid @RequestBody SelfAssessmentRequest request) {
        return ApiResponse.success("Self assessment updated successfully", service.update(id, request), null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete self assessment")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ApiResponse.success("Self assessment deleted successfully", null, null);
    }
}
