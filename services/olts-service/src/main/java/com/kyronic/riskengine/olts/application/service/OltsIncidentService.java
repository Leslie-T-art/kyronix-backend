package com.kyronic.riskengine.olts.application.service;

import com.kyronic.riskengine.common.authorization.AuthorizerCandidate;
import com.kyronic.riskengine.common.authorization.AuthorizerResolutionRequest;
import com.kyronic.riskengine.common.authorization.SegregationOfDutiesPolicy;
import com.kyronic.riskengine.common.authorization.ServerSideAuthorizerResolver;
import com.kyronic.riskengine.common.events.EventEnvelope;
import com.kyronic.riskengine.olts.application.dto.CreateIncidentRequest;
import com.kyronic.riskengine.olts.application.dto.UpdateIncidentRequest;
import com.kyronic.riskengine.olts.domain.model.OltsIncident;

import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class OltsIncidentService {

    private final IncidentIdGenerator incidentIdGenerator;
    private final OltsIncidentStore incidentStore;
    private final ServerSideAuthorizerResolver authorizerResolver;
    private final SegregationOfDutiesPolicy segregationOfDutiesPolicy;
    private final AuthorizationDirectory authorizationDirectory;
    private final EventPublisher eventPublisher;
    private final Clock clock;

    public OltsIncidentService(IncidentIdGenerator incidentIdGenerator,
                               OltsIncidentStore incidentStore,
                               ServerSideAuthorizerResolver authorizerResolver,
                               SegregationOfDutiesPolicy segregationOfDutiesPolicy,
                               AuthorizationDirectory authorizationDirectory,
                               EventPublisher eventPublisher,
                               Clock clock) {
        this.incidentIdGenerator = incidentIdGenerator;
        this.incidentStore = incidentStore;
        this.authorizerResolver = authorizerResolver;
        this.segregationOfDutiesPolicy = segregationOfDutiesPolicy;
        this.authorizationDirectory = authorizationDirectory;
        this.eventPublisher = eventPublisher;
        this.clock = clock;
    }

    public OltsIncident create(CreateIncidentRequest request, UUID inputterUserId, String responsiblePersonName, String correlationId) {
        OltsIncident incident = OltsIncident.create(
                incidentIdGenerator.nextIncidentId(),
                request.departmentId(),
                request.branchId(),
                inputterUserId,
                request.incidentDate(),
                request.discoveryDate(),
                request.lossCategory(),
                request.eventType(),
                request.severity(),
                request.description(),
                request.currencyCode(),
                request.grossLoss(),
                request.recoveries(),
                request.potentialLoss(),
                inputterUserId,
                responsiblePersonName,
                Instant.now(clock)
        );
        incidentStore.save(incident);
        eventPublisher.publish(event("olts.incident.created.v1", incident, correlationId, incident.getInputterUserId(), null));
        return incident;
    }

    public List<OltsIncident> listAll() {
        return incidentStore.findAllActive().stream()
                .sorted(Comparator.comparing(OltsIncident::getCreatedAt).reversed())
                .toList();
    }

    public OltsIncident update(String incidentId, UpdateIncidentRequest request, UUID actorUserId, String responsiblePersonName, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.updateDraft(
                actorUserId,
                request.departmentId(),
                request.branchId(),
                request.incidentDate(),
                request.discoveryDate(),
                request.lossCategory(),
                request.eventType(),
                request.severity(),
                request.description(),
                request.currencyCode(),
                request.grossLoss(),
                request.recoveries(),
                request.potentialLoss(),
                actorUserId,
                responsiblePersonName,
                Instant.now(clock)
        );
        incidentStore.save(incident);
        eventPublisher.publish(event("olts.incident.updated.v1", incident, correlationId, incident.getInputterUserId(), null));
        return incident;
    }

    public void delete(String incidentId, UUID actorUserId, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.markDeleted(actorUserId, Instant.now(clock));
        incidentStore.save(incident);
        eventPublisher.publish(event("olts.incident.deleted.v1", incident, correlationId, incident.getInputterUserId(), null));
    }

    public OltsIncident submit(String incidentId, UUID actorUserId, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.submit(actorUserId, Instant.now(clock));
        incidentStore.save(incident);
        resolveAuthorizer(incident);
        eventPublisher.publish(event("olts.incident.submitted.v1", incident, correlationId, actorUserId, null));
        return incident;
    }

    public OltsIncident startReview(String incidentId, UUID actorUserId, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.beginAuthorizationReview(actorUserId, Instant.now(clock));
        incidentStore.save(incident);
        eventPublisher.publish(event("authorization.review-started.v1", incident, correlationId, incident.getInputterUserId(), actorUserId));
        return incident;
    }

    public OltsIncident approve(String incidentId, UUID actorUserId, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.authorize(actorUserId, Instant.now(clock), segregationOfDutiesPolicy);
        incidentStore.save(incident);
        eventPublisher.publish(event("olts.incident.authorized.v1", incident, correlationId, incident.getInputterUserId(), actorUserId));
        return incident;
    }

    public OltsIncident reject(String incidentId, UUID actorUserId, String reason, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.reject(actorUserId, reason, Instant.now(clock), segregationOfDutiesPolicy);
        incidentStore.save(incident);
        eventPublisher.publish(event("authorization.rejected.v1", incident, correlationId, incident.getInputterUserId(), actorUserId));
        return incident;
    }

    public OltsIncident returnForCorrection(String incidentId, UUID actorUserId, String reason, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.returnForCorrection(actorUserId, reason, Instant.now(clock), segregationOfDutiesPolicy);
        incidentStore.save(incident);
        eventPublisher.publish(event("authorization.returned.v1", incident, correlationId, incident.getInputterUserId(), actorUserId));
        return incident;
    }

    public Optional<OltsIncident> getByIncidentId(String incidentId) {
        return incidentStore.findByIncidentId(incidentId);
    }

    private AuthorizerCandidate resolveAuthorizer(OltsIncident incident) {
        List<AuthorizerCandidate> candidates = authorizationDirectory.findCandidates(incident.getDepartmentId(), "OLTS_AUTHORIZE");
        return authorizerResolver.resolve(new AuthorizerResolutionRequest(
                incident.getDepartmentId(),
                incident.getInputterUserId(),
                incident.getLastModifiedBy(),
                "OLTS_AUTHORIZE",
                Instant.now(clock)
        ), candidates);
    }

    private EventEnvelope event(String eventType, OltsIncident incident, String correlationId, UUID inputterUserId, UUID authorizerUserId) {
        return new EventEnvelope(
                UUID.randomUUID(),
                eventType,
                "v1",
                incident.getId(),
                "OLTS_INCIDENT",
                incident.getIncidentId(),
                incident.getRecordVersion(),
                incident.getDepartmentId(),
                Instant.now(clock),
                correlationId,
                correlationId,
                inputterUserId,
                incident.getInputterUserId(),
                authorizerUserId,
                "olts-service",
                "default",
                Map.of("status", incident.getAuthorizationStatus().name())
        );
    }
}
