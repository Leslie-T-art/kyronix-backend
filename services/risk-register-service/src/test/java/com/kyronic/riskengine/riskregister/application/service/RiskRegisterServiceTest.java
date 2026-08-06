package com.kyronic.riskengine.riskregister.application.service;

import com.kyronic.riskengine.riskregister.application.dto.RiskRecordRequest;
import com.kyronic.riskengine.riskregister.application.dto.RiskRecordResponse;
import com.kyronic.riskengine.riskregister.domain.RiskRecord;
import com.kyronic.riskengine.riskregister.infrastructure.persistence.RiskRecordRepository;
import com.kyronic.riskengine.riskregister.interfaces.RiskRecordNotFoundException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RiskRegisterServiceTest {

    private static final Instant NOW = Instant.parse("2026-08-06T10:15:30Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void createPersistsRiskRecord() {
        InMemoryRiskRecordRepository repository = new InMemoryRiskRecordRepository();
        RiskRegisterService service = service(repository, "RISK-2026-00001", "superadmin");

        RiskRecordResponse response = service.create(request());

        assertThat(repository.records()).hasSize(1);
        RiskRecord saved = repository.records().get(0);
        assertThat(saved.getRiskId()).isEqualTo("RISK-2026-00001");
        assertThat(saved.getRiskTitle()).isEqualTo("Third-party disruption");
        assertThat(saved.getLikelihood()).isEqualTo(4);
        assertThat(saved.getCreatedBy()).isEqualTo("superadmin");
        assertThat(saved.getCreatedAt()).isEqualTo(NOW);
        assertThat(response.riskId()).isEqualTo("RISK-2026-00001");
        assertThat(response.linkedKri()).isEqualTo("KRI-2026-00009");
    }

    @Test
    void listReturnsActiveRiskRecords() {
        InMemoryRiskRecordRepository repository = new InMemoryRiskRecordRepository();
        repository.save(record("RISK-2026-00002"));
        RiskRegisterService service = service(repository, "RISK-2026-00099", "superadmin");

        List<RiskRecordResponse> responses = service.list();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).riskId()).isEqualTo("RISK-2026-00002");
    }

    @Test
    void updateMutatesExistingRiskRecord() {
        InMemoryRiskRecordRepository repository = new InMemoryRiskRecordRepository();
        repository.save(record("RISK-2026-00003"));
        RiskRegisterService service = service(repository, "RISK-2026-00099", "editor");

        RiskRecordRequest request = new RiskRecordRequest(
                "Updated risk",
                "Compliance",
                "Jane Doe",
                "Finance",
                "Updated description",
                2,
                5,
                "High",
                "Control A, Control B",
                "Effective",
                "Medium",
                "Mitigate",
                "Open",
                LocalDate.of(2026, 8, 20),
                "Invoice approval",
                "KRI-2026-00077",
                "Revise sign-off workflow"
        );

        RiskRecordResponse response = service.update("RISK-2026-00003", request);

        RiskRecord saved = repository.findByRiskIdAndDeletedFalse("RISK-2026-00003").orElseThrow();
        assertThat(saved.getRiskTitle()).isEqualTo("Updated risk");
        assertThat(saved.getUpdatedBy()).isEqualTo("editor");
        assertThat(saved.getUpdatedAt()).isEqualTo(NOW);
        assertThat(response.riskTitle()).isEqualTo("Updated risk");
        assertThat(response.linkedProcess()).isEqualTo("Invoice approval");
    }

    @Test
    void deleteMarksRiskRecordDeleted() {
        InMemoryRiskRecordRepository repository = new InMemoryRiskRecordRepository();
        repository.save(record("RISK-2026-00004"));
        RiskRegisterService service = service(repository, "RISK-2026-00099", "deleter");

        service.delete("RISK-2026-00004");

        RiskRecord deleted = repository.findByRiskId("RISK-2026-00004").orElseThrow();
        assertThat(repository.findByRiskIdAndDeletedFalse("RISK-2026-00004")).isEmpty();
        assertThat(deleted.getUpdatedBy()).isEqualTo("deleter");
        assertThat(deleted.getUpdatedAt()).isEqualTo(NOW);
    }

    @Test
    void getThrowsWhenRiskRecordMissing() {
        InMemoryRiskRecordRepository repository = new InMemoryRiskRecordRepository();
        RiskRegisterService service = service(repository, "RISK-2026-00099", "superadmin");

        assertThatThrownBy(() -> service.get("RISK-2026-404"))
                .isInstanceOf(RiskRecordNotFoundException.class)
                .hasMessage("Risk record not found: RISK-2026-404");
    }

    private RiskRegisterService service(InMemoryRiskRecordRepository repository, String nextId, String currentUser) {
        return new RiskRegisterService(
                repository.proxy(),
                new StubRiskIdGenerator(nextId),
                new StubCurrentUserProvider(currentUser),
                CLOCK
        );
    }

    private RiskRecordRequest request() {
        return new RiskRecordRequest(
                "Third-party disruption",
                "Operational",
                "John Doe",
                "Operations",
                "Supplier outage could delay settlement processing",
                4,
                4,
                "Critical",
                "Vendor monitoring; BCP test",
                "Partially effective",
                "High",
                "Mitigate",
                "Open",
                LocalDate.of(2026, 8, 15),
                "Settlement operations",
                "KRI-2026-00009",
                "Add fallback provider and monthly scenario test"
        );
    }

    private RiskRecord record(String riskId) {
        return new RiskRecord(
                UUID.randomUUID(),
                riskId,
                "Liquidity stress",
                "Financial",
                "Owner",
                "Treasury",
                "Daily liquidity position may deteriorate during stress",
                3,
                5,
                "High",
                "Liquidity limits",
                "Effective",
                "Medium",
                "Transfer",
                "Monitoring",
                LocalDate.of(2026, 9, 1),
                "Treasury operations",
                "KRI-2026-00010",
                "Refresh contingency funding plan",
                NOW,
                "creator",
                NOW,
                "creator",
                false,
                null
        );
    }

    private static final class StubCurrentUserProvider extends CurrentUserProvider {
        private final String currentUser;

        private StubCurrentUserProvider(String currentUser) {
            this.currentUser = currentUser;
        }

        @Override
        public String currentUsername() {
            return currentUser;
        }
    }

    private static final class StubRiskIdGenerator extends RiskIdGenerator {
        private final String nextId;

        private StubRiskIdGenerator(String nextId) {
            super(null);
            this.nextId = nextId;
        }

        @Override
        public String nextId() {
            return nextId;
        }
    }

    private static final class InMemoryRiskRecordRepository implements InvocationHandler {
        private final List<RiskRecord> records = new ArrayList<>();
        private final RiskRecordRepository proxy = (RiskRecordRepository) Proxy.newProxyInstance(
                RiskRecordRepository.class.getClassLoader(),
                new Class[]{RiskRecordRepository.class},
                this
        );

        private RiskRecordRepository proxy() {
            return proxy;
        }

        private List<RiskRecord> records() {
            return records;
        }

        private Optional<RiskRecord> findByRiskId(String riskId) {
            return records.stream()
                    .filter(record -> record.getRiskId().equals(riskId))
                    .findFirst();
        }

        @Override
        public Object invoke(Object proxy, java.lang.reflect.Method method, Object[] args) {
            return switch (method.getName()) {
                case "save" -> save((RiskRecord) args[0]);
                case "findByRiskIdAndDeletedFalse" -> findByRiskIdAndDeletedFalse((String) args[0]);
                case "findAllByDeletedFalse" -> findAllByDeletedFalse();
                case "toString" -> "InMemoryRiskRecordRepository";
                case "hashCode" -> System.identityHashCode(this);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException("Unsupported repository method: " + method.getName());
            };
        }

        private RiskRecord save(RiskRecord record) {
            records.removeIf(existing -> existing.getId().equals(record.getId()));
            records.add(record);
            records.sort(Comparator.comparing(RiskRecord::getCreatedAt).reversed());
            return record;
        }

        private Optional<RiskRecord> findByRiskIdAndDeletedFalse(String riskId) {
            return records.stream()
                    .filter(record -> record.getRiskId().equals(riskId))
                    .filter(record -> !record.isDeleted())
                    .findFirst();
        }

        private List<RiskRecord> findAllByDeletedFalse() {
            return records.stream()
                    .filter(record -> !record.isDeleted())
                    .sorted(Comparator.comparing(RiskRecord::getCreatedAt).reversed())
                    .toList();
        }
    }
}
