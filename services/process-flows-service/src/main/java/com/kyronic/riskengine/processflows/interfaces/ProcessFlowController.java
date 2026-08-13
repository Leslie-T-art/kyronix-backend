package com.kyronic.riskengine.processflows.interfaces;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.common.api.PageResponse;
import com.kyronic.riskengine.processflows.application.dto.ProcessFlowRequest;
import com.kyronic.riskengine.processflows.application.dto.ProcessFlowResponse;
import com.kyronic.riskengine.processflows.application.dto.ProcessFlowWorkflowActionRequest;
import com.kyronic.riskengine.processflows.application.service.ProcessFlowService;
import com.kyronic.riskengine.processflows.infrastructure.configuration.ProcessFlowsOpenApiConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('INPUTTER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Create process flow")
    public ApiResponse<ProcessFlowResponse> create(@Valid @ModelAttribute ProcessFlowRequest request) {
        return ApiResponse.success("Process flow created successfully", service.create(request), null);
    }

    @GetMapping
    @PreAuthorize("hasRole('INPUTTER') or hasRole('AUTHORIZER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN') or hasRole('ENTERPRISE_ADMIN') or hasRole('EXECUTIVE')")
    @Operation(summary = "List process flows")
    public ApiResponse<PageResponse<ProcessFlowResponse>> list(@RequestParam(name = "page", defaultValue = "0") int page,
                                                               @RequestParam(name = "size", defaultValue = "20") int size,
                                                               @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                                               @RequestParam(name = "sortDirection", defaultValue = "desc") String sortDirection) {
        return ApiResponse.success("Process flows retrieved successfully", PageResponse.from(service.list(page, size, sortBy, sortDirection)), null);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('INPUTTER') or hasRole('AUTHORIZER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN') or hasRole('ENTERPRISE_ADMIN') or hasRole('EXECUTIVE')")
    @Operation(summary = "Get process flow")
    public ApiResponse<ProcessFlowResponse> get(@PathVariable("id") Long id) {
        return ApiResponse.success("Process flow retrieved successfully", service.get(id), null);
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('INPUTTER') or hasRole('AUTHORIZER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN') or hasRole('ENTERPRISE_ADMIN') or hasRole('EXECUTIVE')")
    @Operation(summary = "Count process flows")
    public ApiResponse<Long> count(@RequestParam(required = false) Long departmentId,
                                   @RequestParam(required = false) String workflowStatus) {
        return ApiResponse.success("Process flow count retrieved successfully", service.count(departmentId, workflowStatus), null);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('INPUTTER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Update process flow")
    public ApiResponse<ProcessFlowResponse> update(@PathVariable("id") Long id,
                                                   @Valid @ModelAttribute ProcessFlowRequest request) {
        return ApiResponse.success("Process flow updated successfully", service.update(id, request), null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INPUTTER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Delete process flow")
    public ApiResponse<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ApiResponse.success("Process flow deleted successfully", null, null);
    }

    @GetMapping("/{id}/document")
    @PreAuthorize("hasRole('INPUTTER') or hasRole('AUTHORIZER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN') or hasRole('ENTERPRISE_ADMIN') or hasRole('EXECUTIVE')")
    @Operation(summary = "Download process flow document")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable("id") Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(service.downloadDocument(id));
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('INPUTTER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Submit process flow for approval")
    public ApiResponse<ProcessFlowResponse> submit(@PathVariable("id") Long id) {
        return ApiResponse.success("Process flow submitted successfully", service.submit(id), null);
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('AUTHORIZER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Approve process flow")
    public ApiResponse<ProcessFlowResponse> approve(@PathVariable("id") Long id,
                                                    @Valid @RequestBody ProcessFlowWorkflowActionRequest request) {
        return ApiResponse.success("Process flow approved successfully", service.approve(id, request.comment()), null);
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('AUTHORIZER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Reject process flow")
    public ApiResponse<ProcessFlowResponse> reject(@PathVariable("id") Long id,
                                                   @Valid @RequestBody ProcessFlowWorkflowActionRequest request) {
        return ApiResponse.success("Process flow rejected successfully", service.reject(id, request.comment()), null);
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('AUTHORIZER') or hasRole('DEPARTMENT_HEAD') or hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Return process flow for correction")
    public ApiResponse<ProcessFlowResponse> returnForCorrection(@PathVariable("id") Long id,
                                                                @Valid @RequestBody ProcessFlowWorkflowActionRequest request) {
        return ApiResponse.success("Process flow returned successfully", service.returnForCorrection(id, request.comment()), null);
    }
}
