package com.kyronic.riskengine.olts.interfaces.rest;

import com.kyronic.riskengine.common.api.ApiResponse;
import com.kyronic.riskengine.olts.application.dto.CreateIncidentRequest;
import com.kyronic.riskengine.olts.application.dto.IncidentResponse;
import com.kyronic.riskengine.olts.application.dto.ReferenceDataOptionResponse;
import com.kyronic.riskengine.olts.application.dto.UpdateIncidentRequest;
import com.kyronic.riskengine.olts.application.dto.WorkflowActionRequest;
import com.kyronic.riskengine.olts.application.mapper.IncidentMapper;
import com.kyronic.riskengine.olts.application.service.AuthReferenceDataGateway;
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
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
@RestController
@RequestMapping("/api/v1/olts/incidents")
@Tag(name = "OLTS Incidents", description = "Operational loss and incident management endpoints")
public class OltsIncidentController {

    private final OltsIncidentService service;
    private final IncidentMapper mapper;
    private final AuthReferenceDataGateway authReferenceDataGateway;

    public OltsIncidentController(OltsIncidentService service,
                                  IncidentMapper mapper,
                                  AuthReferenceDataGateway authReferenceDataGateway) {
        this.service = service;
        this.mapper = mapper;
        this.authReferenceDataGateway = authReferenceDataGateway;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create incident", description = "Create a new OLTS incident draft. The backend generates the incidentId and correlationId.")
    public ApiResponse<IncidentResponse> create(@Valid @RequestBody CreateIncidentRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        AuthenticatedUser actor = AuthenticatedUser.fromJwt(jwt);
        return ApiResponse.success("Incident created successfully",
                enrich(mapper.toResponse(service.create(request, actor.userId(), actor.username(), correlationId)), jwt),
                correlationId);
    }

    @GetMapping
    @Operation(summary = "List incidents", description = "Fetch all non-deleted captured incidents.")
    public ApiResponse<List<IncidentResponse>> list(@AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        List<IncidentResponse> incidents = service.listAll().stream()
                .map(mapper::toResponse)
                .map(response -> enrich(response, jwt))
                .toList();
        return ApiResponse.success("Incidents retrieved successfully", incidents, correlationId);
    }

    @GetMapping("/{incidentId}")
    @Operation(summary = "Get incident", description = "Fetch one captured incident by its system-generated incidentId.")
    public ApiResponse<IncidentResponse> get(@PathVariable("incidentId") String incidentId,
                                             @AuthenticationPrincipal Jwt jwt) {
        String correlationId = correlationId();
        IncidentResponse response = service.getByIncidentId(incidentId)
                .map(mapper::toResponse)
                .map(mapped -> enrich(mapped, jwt))
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
                enrich(mapper.toResponse(service.update(incidentId, request, actor.userId(), actor.username(), correlationId)), jwt),
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
                enrich(mapper.toResponse(service.submit(incidentId, actor.userId(), correlationId)), jwt),
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
                enrich(mapper.toResponse(service.startReview(incidentId, actor.userId(), correlationId)), jwt),
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
                enrich(mapper.toResponse(service.approve(incidentId, actor.userId(), correlationId)), jwt),
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
                enrich(mapper.toResponse(service.reject(incidentId, actor.userId(), request.reason(), correlationId)), jwt),
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
                enrich(mapper.toResponse(service.returnForCorrection(incidentId, actor.userId(), request.reason(), correlationId)), jwt),
                correlationId);
    }

    private IncidentResponse enrich(IncidentResponse response, Jwt jwt) {
        if (jwt == null) {
            return response;
        }
        String authorizationHeader = "Bearer " + jwt.getTokenValue();
        Map<UUID, String> departmentsById = authReferenceDataGateway.listDepartments(authorizationHeader).stream()
                .collect(java.util.stream.Collectors.toMap(ReferenceDataOptionResponse::id, ReferenceDataOptionResponse::name, (left, right) -> left));
        Map<UUID, String> branchesById = authReferenceDataGateway.listBranches(authorizationHeader).stream()
                .collect(java.util.stream.Collectors.toMap(ReferenceDataOptionResponse::id, ReferenceDataOptionResponse::name, (left, right) -> left));
        return new IncidentResponse(
                response.id(),
                response.incidentId(),
                response.departmentId(),
                departmentsById.get(response.departmentId()),
                response.branchId(),
                branchesById.get(response.branchId()),
                response.incidentDate(),
                response.discoveryDate(),
                response.lossCategory(),
                response.eventType(),
                response.severity(),
                response.authorizationStatus(),
                response.status(),
                response.grossLoss(),
                response.recoveries(),
                response.netLoss(),
                response.potentialLoss(),
                response.inputterUserId(),
                response.responsiblePersonId(),
                response.responsiblePersonName(),
                response.createdAt(),
                response.createdBy()
        );
    }

    private String correlationId() {
        return UUID.randomUUID().toString();
    }
}
