package com.kyronic.riskengine.selfassessment.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "self_assessments")
public class SelfAssessmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String rcsaId;

    @Column(nullable = false, length = 80)
    private String assessmentPeriod;

    @Column(nullable = false)
    private Long departmentId;

    @Column(nullable = false, length = 180)
    private String processName;

    @Column(nullable = false, length = 120)
    private String riskRegisterRisk;

    @Column(nullable = false, length = 4000)
    private String riskScenario;

    @Column(nullable = false, length = 2000)
    private String cause;

    @Column(nullable = false, length = 2000)
    private String consequenceImpact;

    @Column(nullable = false)
    private Integer inherentImpact;

    @Column(nullable = false)
    private Integer inherentLikelihood;

    @Column(nullable = false)
    private Integer inherentRiskScore;

    @Column(nullable = false, length = 40)
    private String inherentRiskRating;

    @Column(nullable = false, length = 120)
    private String controlDesignEffectiveness;

    @Column(nullable = false, length = 120)
    private String controlOperatingEffectiveness;

    @Column(nullable = false, length = 120)
    private String overallControlEffectiveness;

    @Column(nullable = false)
    private Integer residualImpact;

    @Column(nullable = false)
    private Integer residualLikelihood;

    @Column(nullable = false)
    private Integer residualRiskScore;

    @Column(nullable = false, length = 40)
    private String residualRiskRating;

    @Column(nullable = false, length = 120)
    private String riskResponse;

    @Column(nullable = false)
    private boolean actionRequired;

    @Column(length = 500)
    private String linkedAction;

    @Column(nullable = false, length = 80)
    private String businessReviewStatus;

    @Column(nullable = false, length = 120)
    private String riskReviewVerification;

    @Column(length = 2000)
    private String riskReviewComment;

    @Column
    private LocalDate dateOfLastReview;

    @Column
    private LocalDate nextReviewDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "self_assessment_linked_controls", joinColumns = @JoinColumn(name = "self_assessment_id"))
    @Column(name = "control_reference", nullable = false, length = 120)
    private Set<String> linkedControls = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "self_assessment_linked_kri", joinColumns = @JoinColumn(name = "self_assessment_id"))
    @Column(name = "kri_reference", nullable = false, length = 120)
    private Set<String> linkedKris = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "self_assessment_linked_olts", joinColumns = @JoinColumn(name = "self_assessment_id"))
    @Column(name = "olts_reference", nullable = false, length = 120)
    private Set<String> linkedOltsEvents = new LinkedHashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "self_assessment_linked_findings", joinColumns = @JoinColumn(name = "self_assessment_id"))
    @Column(name = "finding_reference", nullable = false, length = 120)
    private Set<String> linkedIssuesFindings = new LinkedHashSet<>();

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false, length = 120)
    private String createdBy;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(nullable = false, length = 120)
    private String updatedBy;

    @Version
    private Long version;

    protected SelfAssessmentRecord() {
    }

    public SelfAssessmentRecord(Long id,
                                String rcsaId,
                                String assessmentPeriod,
                                Long departmentId,
                                String processName,
                                String riskRegisterRisk,
                                String riskScenario,
                                String cause,
                                String consequenceImpact,
                                Integer inherentImpact,
                                Integer inherentLikelihood,
                                Integer inherentRiskScore,
                                String inherentRiskRating,
                                String controlDesignEffectiveness,
                                String controlOperatingEffectiveness,
                                String overallControlEffectiveness,
                                Integer residualImpact,
                                Integer residualLikelihood,
                                Integer residualRiskScore,
                                String residualRiskRating,
                                String riskResponse,
                                boolean actionRequired,
                                String linkedAction,
                                String businessReviewStatus,
                                String riskReviewVerification,
                                String riskReviewComment,
                                LocalDate dateOfLastReview,
                                LocalDate nextReviewDate,
                                Set<String> linkedControls,
                                Set<String> linkedKris,
                                Set<String> linkedOltsEvents,
                                Set<String> linkedIssuesFindings,
                                Instant createdAt,
                                String createdBy,
                                Instant updatedAt,
                                String updatedBy,
                                Long version) {
        this.id = id;
        this.rcsaId = rcsaId;
        this.assessmentPeriod = assessmentPeriod;
        this.departmentId = departmentId;
        this.processName = processName;
        this.riskRegisterRisk = riskRegisterRisk;
        this.riskScenario = riskScenario;
        this.cause = cause;
        this.consequenceImpact = consequenceImpact;
        this.inherentImpact = inherentImpact;
        this.inherentLikelihood = inherentLikelihood;
        this.inherentRiskScore = inherentRiskScore;
        this.inherentRiskRating = inherentRiskRating;
        this.controlDesignEffectiveness = controlDesignEffectiveness;
        this.controlOperatingEffectiveness = controlOperatingEffectiveness;
        this.overallControlEffectiveness = overallControlEffectiveness;
        this.residualImpact = residualImpact;
        this.residualLikelihood = residualLikelihood;
        this.residualRiskScore = residualRiskScore;
        this.residualRiskRating = residualRiskRating;
        this.riskResponse = riskResponse;
        this.actionRequired = actionRequired;
        this.linkedAction = linkedAction;
        this.businessReviewStatus = businessReviewStatus;
        this.riskReviewVerification = riskReviewVerification;
        this.riskReviewComment = riskReviewComment;
        this.dateOfLastReview = dateOfLastReview;
        this.nextReviewDate = nextReviewDate;
        this.linkedControls = new LinkedHashSet<>(linkedControls);
        this.linkedKris = new LinkedHashSet<>(linkedKris);
        this.linkedOltsEvents = new LinkedHashSet<>(linkedOltsEvents);
        this.linkedIssuesFindings = new LinkedHashSet<>(linkedIssuesFindings);
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.version = version;
    }

    public void update(String assessmentPeriod,
                       Long departmentId,
                       String processName,
                       String riskRegisterRisk,
                       String riskScenario,
                       String cause,
                       String consequenceImpact,
                       Integer inherentImpact,
                       Integer inherentLikelihood,
                       Integer inherentRiskScore,
                       String inherentRiskRating,
                       String controlDesignEffectiveness,
                       String controlOperatingEffectiveness,
                       String overallControlEffectiveness,
                       Integer residualImpact,
                       Integer residualLikelihood,
                       Integer residualRiskScore,
                       String residualRiskRating,
                       String riskResponse,
                       boolean actionRequired,
                       String linkedAction,
                       String businessReviewStatus,
                       String riskReviewVerification,
                       String riskReviewComment,
                       LocalDate dateOfLastReview,
                       LocalDate nextReviewDate,
                       Set<String> linkedControls,
                       Set<String> linkedKris,
                       Set<String> linkedOltsEvents,
                       Set<String> linkedIssuesFindings,
                       Instant updatedAt,
                       String updatedBy) {
        this.assessmentPeriod = assessmentPeriod;
        this.departmentId = departmentId;
        this.processName = processName;
        this.riskRegisterRisk = riskRegisterRisk;
        this.riskScenario = riskScenario;
        this.cause = cause;
        this.consequenceImpact = consequenceImpact;
        this.inherentImpact = inherentImpact;
        this.inherentLikelihood = inherentLikelihood;
        this.inherentRiskScore = inherentRiskScore;
        this.inherentRiskRating = inherentRiskRating;
        this.controlDesignEffectiveness = controlDesignEffectiveness;
        this.controlOperatingEffectiveness = controlOperatingEffectiveness;
        this.overallControlEffectiveness = overallControlEffectiveness;
        this.residualImpact = residualImpact;
        this.residualLikelihood = residualLikelihood;
        this.residualRiskScore = residualRiskScore;
        this.residualRiskRating = residualRiskRating;
        this.riskResponse = riskResponse;
        this.actionRequired = actionRequired;
        this.linkedAction = linkedAction;
        this.businessReviewStatus = businessReviewStatus;
        this.riskReviewVerification = riskReviewVerification;
        this.riskReviewComment = riskReviewComment;
        this.dateOfLastReview = dateOfLastReview;
        this.nextReviewDate = nextReviewDate;
        this.linkedControls = new LinkedHashSet<>(linkedControls);
        this.linkedKris = new LinkedHashSet<>(linkedKris);
        this.linkedOltsEvents = new LinkedHashSet<>(linkedOltsEvents);
        this.linkedIssuesFindings = new LinkedHashSet<>(linkedIssuesFindings);
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public Long getId() { return id; }
    public String getRcsaId() { return rcsaId; }
    public String getAssessmentPeriod() { return assessmentPeriod; }
    public Long getDepartmentId() { return departmentId; }
    public String getProcessName() { return processName; }
    public String getRiskRegisterRisk() { return riskRegisterRisk; }
    public String getRiskScenario() { return riskScenario; }
    public String getCause() { return cause; }
    public String getConsequenceImpact() { return consequenceImpact; }
    public Integer getInherentImpact() { return inherentImpact; }
    public Integer getInherentLikelihood() { return inherentLikelihood; }
    public Integer getInherentRiskScore() { return inherentRiskScore; }
    public String getInherentRiskRating() { return inherentRiskRating; }
    public String getControlDesignEffectiveness() { return controlDesignEffectiveness; }
    public String getControlOperatingEffectiveness() { return controlOperatingEffectiveness; }
    public String getOverallControlEffectiveness() { return overallControlEffectiveness; }
    public Integer getResidualImpact() { return residualImpact; }
    public Integer getResidualLikelihood() { return residualLikelihood; }
    public Integer getResidualRiskScore() { return residualRiskScore; }
    public String getResidualRiskRating() { return residualRiskRating; }
    public String getRiskResponse() { return riskResponse; }
    public boolean isActionRequired() { return actionRequired; }
    public String getLinkedAction() { return linkedAction; }
    public String getBusinessReviewStatus() { return businessReviewStatus; }
    public String getRiskReviewVerification() { return riskReviewVerification; }
    public String getRiskReviewComment() { return riskReviewComment; }
    public LocalDate getDateOfLastReview() { return dateOfLastReview; }
    public LocalDate getNextReviewDate() { return nextReviewDate; }
    public Set<String> getLinkedControls() { return linkedControls; }
    public Set<String> getLinkedKris() { return linkedKris; }
    public Set<String> getLinkedOltsEvents() { return linkedOltsEvents; }
    public Set<String> getLinkedIssuesFindings() { return linkedIssuesFindings; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
}
