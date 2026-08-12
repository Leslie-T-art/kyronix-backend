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
    private final LossCategoryCatalog lossCategoryCatalog;
    private final Clock clock;

    public OltsIncidentService(IncidentIdGenerator incidentIdGenerator,
                               OltsIncidentStore incidentStore,
                               ServerSideAuthorizerResolver authorizerResolver,
                               SegregationOfDutiesPolicy segregationOfDutiesPolicy,
                               AuthorizationDirectory authorizationDirectory,
                               EventPublisher eventPublisher,
                               LossCategoryCatalog lossCategoryCatalog,
                               Clock clock) {
        this.incidentIdGenerator = incidentIdGenerator;
        this.incidentStore = incidentStore;
        this.authorizerResolver = authorizerResolver;
        this.segregationOfDutiesPolicy = segregationOfDutiesPolicy;
        this.authorizationDirectory = authorizationDirectory;
        this.eventPublisher = eventPublisher;
        this.lossCategoryCatalog = lossCategoryCatalog;
        this.clock = clock;
    }

    public OltsIncident create(CreateIncidentRequest request, Long inputterUserId, String responsiblePersonName, String correlationId) {
        OltsIncident incident = OltsIncident.create(
                incidentIdGenerator.nextIncidentId(),
                inputterUserId,
                responsiblePersonName,
                request.eventStatusId(),
                request.incidentDate(),
                request.incidentEndDate(),
                request.detectionDate(),
                request.departmentId(),
                request.branchId(),
                request.eventTitle(),
                request.processName(),
                request.productService(),
                request.baselEventCategoryId(),
                request.eventDescription(),
                request.immediateActionTaken(),
                request.rootCauseCategoryId(),
                request.rootCauseDescription(),
                request.controlId(),
                request.failedMissingControl(),
                request.currencyId(),
                request.grossLoss(),
                request.restitutionRemediationCost(),
                request.recoveryMethodId(),
                request.accountingGlReference(),
                request.dataSourceId(),
                request.nonFinancialImpactType(),
                request.nonFinancialImpactDetails(),
                request.overallEventSeverity(),
                request.correctiveAction(),
                request.actionOwner(),
                request.actionTargetDate(),
                request.actionStatusId(),
                request.preventiveControlImplemented(),
                request.validationEvidence(),
                request.closureValidationDate(),
                request.closureComment(),
                Instant.now(clock)
        );
        incidentStore.save(incident);
        eventPublisher.publish(event("olts.incident.created.v1", incident, correlationId, incident.getInputterUserId(), null));
        return incident;
    }

    public List<OltsIncident> listAll() {
        return incidentStore.findAll().stream()
                .sorted(Comparator.comparing(OltsIncident::getCreatedAt).reversed())
                .toList();
    }

    public OltsIncident update(String incidentId, UpdateIncidentRequest request, Long actorUserId, String responsiblePersonName, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.updateDraft(
                actorUserId,
                responsiblePersonName,
                request.eventTitle(),
                request.eventStatusId(),
                request.incidentDate(),
                request.incidentEndDate(),
                request.detectionDate(),
                request.departmentId(),
                request.branchId(),
                request.processName(),
                request.productService(),
                request.baselEventCategoryId(),
                request.eventDescription(),
                request.immediateActionTaken(),
                request.rootCauseCategoryId(),
                request.rootCauseDescription(),
                request.controlId(),
                request.failedMissingControl(),
                request.currencyId(),
                request.grossLoss(),
                request.restitutionRemediationCost(),
                request.recoveryMethodId(),
                request.accountingGlReference(),
                request.dataSourceId(),
                request.nonFinancialImpactType(),
                request.nonFinancialImpactDetails(),
                request.overallEventSeverity(),
                request.correctiveAction(),
                request.actionOwner(),
                request.actionTargetDate(),
                request.actionStatusId(),
                request.preventiveControlImplemented(),
                request.validationEvidence(),
                request.closureValidationDate(),
                request.closureComment(),
                Instant.now(clock)
        );
        incidentStore.save(incident);
        eventPublisher.publish(event("olts.incident.updated.v1", incident, correlationId, incident.getInputterUserId(), null));
        return incident;
    }

    public void delete(String incidentId, Long actorUserId, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incidentStore.delete(incident);
        eventPublisher.publish(event("olts.incident.deleted.v1", incident, correlationId, incident.getInputterUserId(), null));
    }

    public OltsIncident submit(String incidentId, Long actorUserId, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.submit(actorUserId, Instant.now(clock));
        incidentStore.save(incident);
        AuthorizerCandidate authorizer = resolveAuthorizer(incident);
        eventPublisher.publish(event("authorization.requested.v1", incident, correlationId, actorUserId, authorizer.userId()));
        eventPublisher.publish(event("olts.incident.submitted.v1", incident, correlationId, actorUserId, authorizer.userId()));
        return incident;
    }

    public OltsIncident startReview(String incidentId, Long actorUserId, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.beginAuthorizationReview(actorUserId, Instant.now(clock));
        incidentStore.save(incident);
        eventPublisher.publish(event("authorization.review-started.v1", incident, correlationId, incident.getInputterUserId(), actorUserId));
        return incident;
    }

    public OltsIncident approve(String incidentId, Long actorUserId, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.authorize(actorUserId, Instant.now(clock), segregationOfDutiesPolicy);
        incidentStore.save(incident);
        eventPublisher.publish(event("authorization.approved.v1", incident, correlationId, incident.getInputterUserId(), actorUserId));
        eventPublisher.publish(event("olts.incident.authorized.v1", incident, correlationId, incident.getInputterUserId(), actorUserId));
        return incident;
    }

    public OltsIncident reject(String incidentId, Long actorUserId, String reason, String correlationId) {
        OltsIncident incident = incidentStore.findByIncidentId(incidentId)
                .orElseThrow(() -> new IllegalArgumentException("incident not found"));
        incident.reject(actorUserId, reason, Instant.now(clock), segregationOfDutiesPolicy);
        incidentStore.save(incident);
        eventPublisher.publish(event("authorization.rejected.v1", incident, correlationId, incident.getInputterUserId(), actorUserId));
        return incident;
    }

    public OltsIncident returnForCorrection(String incidentId, Long actorUserId, String reason, String correlationId) {
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

    private EventEnvelope event(String eventType, OltsIncident incident, String correlationId, Long inputterUserId, Long authorizerUserId) {
        return new EventEnvelope(
                UUID.randomUUID(),
                eventType,
                "v1",
                incident.getId(),
                "OLTS_INCIDENT",
                incident.getIncidentId(),
                incident.getRecordVersion(),
                null,
                Instant.now(clock),
                correlationId,
                correlationId,
                null,
                null,
                null,
                "olts-service",
                "default",
                Map.of("status", incident.getAuthorizationStatus().name())
        );
    }
}
