package com.kyronic.riskengine.selfassessment.application.service;

import com.kyronic.riskengine.selfassessment.application.dto.SelfAssessmentRequest;
import com.kyronic.riskengine.selfassessment.domain.SelfAssessmentRecord;
import com.kyronic.riskengine.selfassessment.infrastructure.persistence.SelfAssessmentRepository;
import com.kyronic.riskengine.selfassessment.interfaces.SelfAssessmentValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelfAssessmentServiceTest {

    @Test
    void createCalculatesScoresAndBusinessId() {
        List<SelfAssessmentRecord> storedRecords = new ArrayList<>();
        SelfAssessmentService service = new SelfAssessmentService(
                repository(storedRecords),
                new FixedRcsaIdGenerator("RCSA-2026-000001"),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.fixed(Instant.parse("2026-08-12T10:15:30Z"), ZoneOffset.UTC)
        );

        var response = service.create(request());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.rcsaId()).isEqualTo("RCSA-2026-000001");
        assertThat(response.inherentRiskScore()).isEqualTo(12);
        assertThat(response.residualRiskScore()).isEqualTo(6);
        assertThat(response.createdBy()).isEqualTo("risk.inputter");
    }

    @Test
    void updateUsesIntegerIdAndRecalculatesScores() {
        List<SelfAssessmentRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        SelfAssessmentService service = new SelfAssessmentService(
                repository(storedRecords),
                new FixedRcsaIdGenerator("RCSA-2026-000001"),
                new FixedCurrentUserProvider("dept.head"),
                Clock.fixed(Instant.parse("2026-08-12T11:00:00Z"), ZoneOffset.UTC)
        );

        var response = service.update(1L, updatedRequest());

        assertThat(response.updatedBy()).isEqualTo("dept.head");
        assertThat(response.inherentRiskScore()).isEqualTo(20);
        assertThat(response.inherentRiskRating()).isEqualTo("HIGH");
    }

    @Test
    void listReturnsPaginatedContent() {
        List<SelfAssessmentRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        SelfAssessmentService service = new SelfAssessmentService(
                repository(storedRecords),
                new FixedRcsaIdGenerator("RCSA-2026-000001"),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.systemUTC()
        );

        var page = service.list(0, 20, "createdAt", "desc");

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void countSupportsDepartmentFilter() {
        List<SelfAssessmentRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        SelfAssessmentService service = new SelfAssessmentService(
                repository(storedRecords),
                new FixedRcsaIdGenerator("RCSA-2026-000001"),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.systemUTC()
        );

        assertThat(service.count(4L)).isEqualTo(1L);
        assertThat(service.count(99L)).isEqualTo(0L);
    }

    @Test
    void createRejectsMissingLinkedActionWhenActionRequired() {
        List<SelfAssessmentRecord> storedRecords = new ArrayList<>();
        SelfAssessmentService service = new SelfAssessmentService(
                repository(storedRecords),
                new FixedRcsaIdGenerator("RCSA-2026-000001"),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.systemUTC()
        );

        assertThatThrownBy(() -> service.create(new SelfAssessmentRequest(
                "2026-Q3", 4L, "Payments", "RISK-17", "Scenario", "Cause", "Impact",
                3, 4, Set.of("CTRL-1"), "Effective", "Effective", "Effective",
                2, 3, "MITIGATE", true, null, Set.of("KRI-1"), Set.of("OLTS-1"), Set.of("ISSUE-1"),
                "PENDING", "VERIFIED", null, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 30)
        ))).isInstanceOf(SelfAssessmentValidationException.class);
    }

    @SuppressWarnings("unchecked")
    private SelfAssessmentRepository repository(List<SelfAssessmentRecord> storedRecords) {
        AtomicLong ids = new AtomicLong(storedRecords.size());
        return (SelfAssessmentRepository) Proxy.newProxyInstance(
                SelfAssessmentRepository.class.getClassLoader(),
                new Class<?>[]{SelfAssessmentRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        SelfAssessmentRecord record = (SelfAssessmentRecord) args[0];
                        if (record.getId() == null) {
                            record = new SelfAssessmentRecord(
                                    ids.incrementAndGet(),
                                    record.getRcsaId(),
                                    record.getAssessmentPeriod(),
                                    record.getDepartmentId(),
                                    record.getProcessName(),
                                    record.getRiskRegisterRisk(),
                                    record.getRiskScenario(),
                                    record.getCause(),
                                    record.getConsequenceImpact(),
                                    record.getInherentImpact(),
                                    record.getInherentLikelihood(),
                                    record.getInherentRiskScore(),
                                    record.getInherentRiskRating(),
                                    record.getControlDesignEffectiveness(),
                                    record.getControlOperatingEffectiveness(),
                                    record.getOverallControlEffectiveness(),
                                    record.getResidualImpact(),
                                    record.getResidualLikelihood(),
                                    record.getResidualRiskScore(),
                                    record.getResidualRiskRating(),
                                    record.getRiskResponse(),
                                    record.isActionRequired(),
                                    record.getLinkedAction(),
                                    record.getBusinessReviewStatus(),
                                    record.getRiskReviewVerification(),
                                    record.getRiskReviewComment(),
                                    record.getDateOfLastReview(),
                                    record.getNextReviewDate(),
                                    record.getLinkedControls(),
                                    record.getLinkedKris(),
                                    record.getLinkedOltsEvents(),
                                    record.getLinkedIssuesFindings(),
                                    record.getCreatedAt(),
                                    record.getCreatedBy(),
                                    record.getUpdatedAt(),
                                    record.getUpdatedBy(),
                                    0L
                            );
                        }
                        SelfAssessmentRecord finalRecord = record;
                        storedRecords.removeIf(existing -> existing.getId().equals(finalRecord.getId()));
                        storedRecords.add(finalRecord);
                        yield finalRecord;
                    }
                    case "findById" -> storedRecords.stream().filter(record -> record.getId().equals(args[0])).findFirst();
                    case "findAll" -> {
                        if (args != null && args.length == 1 && args[0] instanceof Pageable pageable) {
                            List<SelfAssessmentRecord> content = storedRecords.stream().toList();
                            yield new PageImpl<>(content, pageable, content.size());
                        }
                        yield storedRecords.stream().toList();
                    }
                    case "count" -> (long) storedRecords.size();
                    case "countByDepartmentId" -> storedRecords.stream().filter(record -> record.getDepartmentId().equals(args[0])).count();
                    case "delete" -> {
                        SelfAssessmentRecord record = (SelfAssessmentRecord) args[0];
                        storedRecords.removeIf(existing -> existing.getId().equals(record.getId()));
                        yield null;
                    }
                    case "toString" -> "FakeSelfAssessmentRepository";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private SelfAssessmentRecord existingRecord() {
        return new SelfAssessmentRecord(
                1L,
                "RCSA-2026-000001",
                "2026-Q3",
                4L,
                "Payments",
                "RISK-17",
                "Scenario",
                "Cause",
                "Impact",
                3,
                4,
                12,
                "MEDIUM",
                "Adequate",
                "Adequate",
                "Adequate",
                2,
                3,
                6,
                "LOW",
                "MITIGATE",
                true,
                "Enhance maker-checker",
                "PENDING",
                "VERIFIED",
                "Comment",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 9, 1),
                new LinkedHashSet<>(Set.of("CTRL-1")),
                new LinkedHashSet<>(Set.of("KRI-1")),
                new LinkedHashSet<>(Set.of("OLTS-1")),
                new LinkedHashSet<>(Set.of("ISSUE-1")),
                Instant.parse("2026-08-10T09:00:00Z"),
                "system.admin",
                Instant.parse("2026-08-10T09:00:00Z"),
                "system.admin",
                0L
        );
    }

    private SelfAssessmentRequest request() {
        return new SelfAssessmentRequest(
                "2026-Q3", 4L, "Payments", "RISK-17", "Scenario", "Cause", "Impact",
                3, 4, Set.of("CTRL-1"), "Adequate", "Adequate", "Adequate",
                2, 3, "MITIGATE", true, "Enhance maker-checker", Set.of("KRI-1"), Set.of("OLTS-1"), Set.of("ISSUE-1"),
                "PENDING", "VERIFIED", "Comment", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 9, 1)
        );
    }

    private SelfAssessmentRequest updatedRequest() {
        return new SelfAssessmentRequest(
                "2026-Q4", 4L, "Payments", "RISK-17", "Scenario", "Cause", "Impact",
                4, 5, Set.of("CTRL-1"), "Adequate", "Adequate", "Adequate",
                3, 4, "MITIGATE", true, "Enhance maker-checker", Set.of("KRI-1"), Set.of("OLTS-1"), Set.of("ISSUE-1"),
                "PENDING", "VERIFIED", "Comment", LocalDate.of(2026, 8, 1), LocalDate.of(2026, 10, 1)
        );
    }

    private static final class FixedRcsaIdGenerator extends RcsaIdGenerator {
        private final String nextId;

        private FixedRcsaIdGenerator(String nextId) {
            super(null);
            this.nextId = nextId;
        }

        @Override
        public String nextId() {
            return nextId;
        }
    }

    private static final class FixedCurrentUserProvider extends CurrentUserProvider {
        private final String username;

        private FixedCurrentUserProvider(String username) {
            this.username = username;
        }

        @Override
        public String currentUsername() {
            return username;
        }
    }
}
