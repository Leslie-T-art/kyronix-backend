package com.kyronic.riskengine.processflows.interfaces;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.common.api.PageResponse;
import com.kyronic.riskengine.processflows.application.dto.ProcessFlowRequest;
import com.kyronic.riskengine.processflows.application.dto.ProcessFlowResponse;
import com.kyronic.riskengine.processflows.application.service.ProcessFlowService;
import com.kyronic.riskengine.processflows.infrastructure.configuration.ProcessFlowsOpenApiConfiguration;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/process-flows")
@Tag(name = "Process Flows", description = "CRUD endpoints for process flow records.")
@SecurityRequirement(name = ProcessFlowsOpenApiConfiguration.BEARER_SCHEME)
public class ProcessFlowController {

    private final ProcessFlowService service;

    public ProcessFlowController(ProcessFlowService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create process flow")
    public ApiResponse<ProcessFlowResponse> create(@Valid @RequestBody ProcessFlowRequest request) {
        return ApiResponse.success("Process flow created successfully", service.create(request), null);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List process flows")
    public ApiResponse<PageResponse<ProcessFlowResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size,
                                                               @RequestParam(defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(defaultValue = "desc") String sortDirection) {
        return ApiResponse.success("Process flows retrieved successfully", PageResponse.from(service.list(page, size, sortBy, sortDirection)), null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get process flow")
    public ApiResponse<ProcessFlowResponse> get(@PathVariable Long id) {
        return ApiResponse.success("Process flow retrieved successfully", service.get(id), null);
    }

    @GetMapping("/count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Count process flows")
    public ApiResponse<Long> count(@RequestParam(required = false) Long departmentId,
                                   @RequestParam(required = false) String status) {
        return ApiResponse.success("Process flow count retrieved successfully", service.count(departmentId, status), null);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update process flow")
    public ApiResponse<ProcessFlowResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody ProcessFlowRequest request) {
        return ApiResponse.success("Process flow updated successfully", service.update(id, request), null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete process flow")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success("Process flow deleted successfully", null, null);
    }
}
