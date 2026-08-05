package com.kyronic.riskengine.olts.interfaces.rest;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.CreateIncidentRequest;
import com.kyronic.riskengine.olts.application.dto.IncidentResponse;
import com.kyronic.riskengine.olts.application.dto.UpdateIncidentRequest;
import com.kyronic.riskengine.olts.application.dto.WorkflowActionRequest;
import com.kyronic.riskengine.olts.application.mapper.IncidentMapper;
import com.kyronic.riskengine.olts.application.service.OltsIncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/olts/incidents")
@Tag(name = "OLTS Incidents", description = "Operational loss and incident management endpoints")
public class OltsIncidentController {

    private final OltsIncidentService service;
    private final IncidentMapper mapper;

    public OltsIncidentController(OltsIncidentService service, IncidentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create incident", description = "Create a new OLTS incident draft. The backend generates the incidentId and correlationId.")
    public ApiResponse<IncidentResponse> create(@Valid @RequestBody CreateIncidentRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        AuthenticatedUser actor = AuthenticatedUser.fromJwt(jwt);
        return ApiResponse.success("Incident created successfully",
                mapper.toResponse(service.create(request, actor.userId(), actor.username(), correlationId)),
                correlationId);
    }

    @GetMapping
    @Operation(summary = "List incidents", description = "Fetch all non-deleted captured incidents.")
    public ApiResponse<List<IncidentResponse>> list() {
        String correlationId = correlationId();
        List<IncidentResponse> incidents = service.listAll().stream()
                .map(mapper::toResponse)
                .toList();
        return ApiResponse.success("Incidents retrieved successfully", incidents, correlationId);
    }

    @GetMapping("/{incidentId}")
    @Operation(summary = "Get incident", description = "Fetch one captured incident by its system-generated incidentId.")
    public ApiResponse<IncidentResponse> get(@PathVariable("incidentId") String incidentId) {
        String correlationId = correlationId();
        IncidentResponse response = service.getByIncidentId(incidentId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        return ApiResponse.success("Incident retrieved successfully", response, correlationId);
    }

    @PutMapping("/{incidentId}")
    @Operation(summary = "Update incident", description = "Update a draft OLTS incident by its system-generated incidentId.")
    public ApiResponse<IncidentResponse> update(@PathVariable("incidentId") String incidentId,
                                                @Valid @RequestBody UpdateIncidentRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        AuthenticatedUser actor = AuthenticatedUser.fromJwt(jwt);
        return ApiResponse.success("Incident updated successfully",
                mapper.toResponse(service.update(incidentId, request, actor.userId(), actor.username(), correlationId)),
                correlationId);
    }

    @DeleteMapping("/{incidentId}")
    @Operation(summary = "Delete incident", description = "Soft delete a draft OLTS incident by its system-generated incidentId.")
    public ApiResponse<Void> delete(@PathVariable("incidentId") String incidentId, @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        service.delete(incidentId, AuthenticatedUser.fromJwt(jwt).userId(), correlationId);
        return ApiResponse.success("Incident deleted successfully", null, correlationId);
    }

    @PostMapping("/{incidentId}/submit")
    @Operation(summary = "Submit incident", description = "Submit a draft incident for department-head authorization.")
    public ApiResponse<IncidentResponse> submit(@PathVariable("incidentId") String incidentId,
                                                @Valid @RequestBody WorkflowActionRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        AuthenticatedUser actor = AuthenticatedUser.fromJwt(jwt);
        return ApiResponse.success("Incident submitted successfully",
                mapper.toResponse(service.submit(incidentId, actor.userId(), correlationId)),
                correlationId);
    }

    @PostMapping("/{incidentId}/authorization/start")
    @Operation(summary = "Start authorization review", description = "Move a pending incident into authorization review.")
    public ApiResponse<IncidentResponse> startReview(@PathVariable("incidentId") String incidentId,
                                                     @Valid @RequestBody WorkflowActionRequest request,
                                                     @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        AuthenticatedUser actor = AuthenticatedUser.fromJwt(jwt);
        return ApiResponse.success("Authorization review started successfully",
                mapper.toResponse(service.startReview(incidentId, actor.userId(), correlationId)),
                correlationId);
    }

    @PostMapping("/{incidentId}/authorization/approve")
    @Operation(summary = "Approve incident", description = "Authorize a submitted incident as the resolved department head or delegate.")
    public ApiResponse<IncidentResponse> approve(@PathVariable("incidentId") String incidentId,
                                                 @Valid @RequestBody WorkflowActionRequest request,
                                                 @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        AuthenticatedUser actor = AuthenticatedUser.fromJwt(jwt);
        return ApiResponse.success("Incident authorized successfully",
                mapper.toResponse(service.approve(incidentId, actor.userId(), correlationId)),
                correlationId);
    }

    @PostMapping("/{incidentId}/authorization/reject")
    @Operation(summary = "Reject incident", description = "Reject a submitted incident. A reason is required.")
    public ApiResponse<IncidentResponse> reject(@PathVariable("incidentId") String incidentId,
                                                @Valid @RequestBody WorkflowActionRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        AuthenticatedUser actor = AuthenticatedUser.fromJwt(jwt);
        return ApiResponse.success("Incident rejected successfully",
                mapper.toResponse(service.reject(incidentId, actor.userId(), request.reason(), correlationId)),
                correlationId);
    }

    @PostMapping("/{incidentId}/authorization/return")
    @Operation(summary = "Return incident for correction", description = "Return a submitted incident to the inputter for correction. A reason is required.")
    public ApiResponse<IncidentResponse> returnForCorrection(@PathVariable("incidentId") String incidentId,
                                                             @Valid @RequestBody WorkflowActionRequest request,
                                                             @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        AuthenticatedUser actor = AuthenticatedUser.fromJwt(jwt);
        return ApiResponse.success("Incident returned successfully",
                mapper.toResponse(service.returnForCorrection(incidentId, actor.userId(), request.reason(), correlationId)),
                correlationId);
    }

    private String correlationId() {
        return UUID.randomUUID().toString();
    }
}
