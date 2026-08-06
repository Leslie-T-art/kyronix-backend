package com.kyronic.riskengine.olts.interfaces.rest;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.ReferenceDataOptionResponse;
import com.kyronic.riskengine.olts.application.service.AuthReferenceDataGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/olts")
@Tag(name = "OLTS Reference Data", description = "Lookup endpoints for OLTS dropdown selections.")
public class OltsReferenceDataController {

    private final AuthReferenceDataGateway authReferenceDataGateway;

    public OltsReferenceDataController(AuthReferenceDataGateway authReferenceDataGateway) {
        this.authReferenceDataGateway = authReferenceDataGateway;
    }

    @GetMapping("/departments")
    @Operation(summary = "List departments", description = "Fetch departments available to the logged-in user for OLTS capture.")
    public ApiResponse<List<ReferenceDataOptionResponse>> listDepartments(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            HttpServletRequest request) {
        return ApiResponse.success("Departments retrieved successfully",
                authReferenceDataGateway.listDepartments(authorizationHeader), correlationId(request));
    }

    @GetMapping("/branches")
    @Operation(summary = "List branches", description = "Fetch branches available to the logged-in user for OLTS capture.")
    public ApiResponse<List<ReferenceDataOptionResponse>> listBranches(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            HttpServletRequest request) {
        return ApiResponse.success("Branches retrieved successfully",
                authReferenceDataGateway.listBranches(authorizationHeader), correlationId(request));
    }

    @GetMapping("/event-types")
    @Operation(summary = "List event types", description = "Fetch configured event types available to the logged-in user for OLTS capture.")
    public ApiResponse<List<ReferenceDataOptionResponse>> listEventTypes(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            HttpServletRequest request) {
        return ApiResponse.success("Event types retrieved successfully",
                authReferenceDataGateway.listEventTypes(authorizationHeader), correlationId(request));
    }

    private String correlationId(HttpServletRequest request) {
        String header = request.getHeader("X-Correlation-Id");
        return header == null || header.isBlank() ? UUID.randomUUID().toString() : header;
    }
}
