package com.kyronic.riskengine.kri.application.service;

import com.kyronic.riskengine.kri.application.dto.KriRequest;
import com.kyronic.riskengine.kri.domain.KriRecord;
import com.kyronic.riskengine.kri.infrastructure.persistence.KriRepository;
import com.kyronic.riskengine.kri.interfaces.KriNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KriServiceTest {

    @Test
    void createGeneratesBackendKriId() {
        List<KriRecord> storedRecords = new ArrayList<>();
        KriRepository repository = repository(storedRecords);
        FixedKriIdGenerator kriIdGenerator = new FixedKriIdGenerator("KRI-2026-00001");
        FixedCurrentUserProvider currentUserProvider = new FixedCurrentUserProvider("risk.inputter");
        Clock clock = Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC);
        KriService service = new KriService(repository, kriIdGenerator, currentUserProvider, clock);

        var response = service.create(request());

        assertThat(response.kriId()).isEqualTo("KRI-2026-00001");
        assertThat(response.createdBy()).isEqualTo("risk.inputter");
        assertThat(response.indicatorName()).isEqualTo("Loan Default Ratio");
    }

    @Test
    void updateChangesStoredKri() {
        List<KriRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        KriRepository repository = repository(storedRecords);
        FixedKriIdGenerator kriIdGenerator = new FixedKriIdGenerator("KRI-2026-00001");
        FixedCurrentUserProvider currentUserProvider = new FixedCurrentUserProvider("dept.head");
        Clock clock = Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC);
        KriService service = new KriService(repository, kriIdGenerator, currentUserProvider, clock);

        var response = service.update("KRI-2026-00001", updatedRequest());

        assertThat(response.owner()).isEqualTo("Treasury Director");
        assertThat(response.updatedBy()).isEqualTo("dept.head");
        assertThat(response.currentValue()).isEqualByComparingTo("8.5000");
    }

    @Test
    void deleteRemovesKri() {
        List<KriRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        KriRepository repository = repository(storedRecords);
        FixedKriIdGenerator kriIdGenerator = new FixedKriIdGenerator("KRI-2026-00001");
        FixedCurrentUserProvider currentUserProvider = new FixedCurrentUserProvider("risk.inputter");
        Clock clock = Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC);
        KriService service = new KriService(repository, kriIdGenerator, currentUserProvider, clock);

        service.delete("KRI-2026-00001");

        assertThat(storedRecords).isEmpty();
    }

    @Test
    void listReturnsActiveKris() {
        List<KriRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        KriRepository repository = repository(storedRecords);
        FixedKriIdGenerator kriIdGenerator = new FixedKriIdGenerator("KRI-2026-00001");
        FixedCurrentUserProvider currentUserProvider = new FixedCurrentUserProvider("risk.inputter");
        Clock clock = Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC);
        KriService service = new KriService(repository, kriIdGenerator, currentUserProvider, clock);

        var results = service.list();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).kriId()).isEqualTo("KRI-2026-00001");
    }

    @Test
    void getThrowsWhenKriMissing() {
        KriRepository repository = repository(new ArrayList<>());
        FixedKriIdGenerator kriIdGenerator = new FixedKriIdGenerator("KRI-2026-00001");
        FixedCurrentUserProvider currentUserProvider = new FixedCurrentUserProvider("risk.inputter");
        Clock clock = Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC);
        KriService service = new KriService(repository, kriIdGenerator, currentUserProvider, clock);

        assertThatThrownBy(() -> service.get("KRI-2026-99999"))
                .isInstanceOf(KriNotFoundException.class)
                .hasMessage("KRI not found: KRI-2026-99999");
    }

    @SuppressWarnings("unchecked")
    private KriRepository repository(List<KriRecord> storedRecords) {
        return (KriRepository) Proxy.newProxyInstance(
                KriRepository.class.getClassLoader(),
                new Class<?>[]{KriRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        KriRecord record = (KriRecord) args[0];
                        storedRecords.removeIf(existing -> existing.getId().equals(record.getId()));
                        storedRecords.add(record);
                        yield record;
                    }
                    case "findByKriId" -> storedRecords.stream()
                            .filter(record -> record.getKriId().equals(args[0]))
                            .findFirst();
                    case "findAll" -> storedRecords.stream()
                            .toList();
                    case "delete" -> {
                        KriRecord record = (KriRecord) args[0];
                        storedRecords.removeIf(existing -> existing.getId().equals(record.getId()));
                        yield null;
                    }
                    case "toString" -> "FakeKriRepository";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName() + " " + (args == null ? 0 : args.length));
                }
        );
    }

    private static final class FixedKriIdGenerator extends KriIdGenerator {
        private final String nextId;

        private FixedKriIdGenerator(String nextId) {
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

    private KriRecord existingRecord() {
        return new KriRecord(
                1L,
                "KRI-2026-00001",
                "Loan Default Ratio",
                "Credit",
                "Head of Credit",
                "Retail Banking",
                "MONTHLY",
                "Tracks default exposure for the retail loan book.",
                "PERCENTAGE",
                new BigDecimal("5.0000"),
                "LOWER_IS_BETTER",
                new BigDecimal("4.0000"),
                new BigDecimal("6.0000"),
                new BigDecimal("8.0000"),
                new BigDecimal("5.5000"),
                "Core Banking",
                LocalDate.of(2026, 8, 30),
                "RISK-001",
                "Risk Committee",
                "Current value exceeds red threshold",
                Instant.parse("2026-08-01T10:00:00Z"),
                "system.admin",
                Instant.parse("2026-08-01T10:00:00Z"),
                "system.admin",
                0L
        );
    }

    private KriRequest request() {
        return new KriRequest(
                "Loan Default Ratio",
                "Credit",
                "Head of Credit",
                "Retail Banking",
                "MONTHLY",
                "Tracks default exposure for the retail loan book.",
                "PERCENTAGE",
                new BigDecimal("5.0000"),
                "LOWER_IS_BETTER",
                new BigDecimal("4.0000"),
                new BigDecimal("6.0000"),
                new BigDecimal("8.0000"),
                new BigDecimal("5.5000"),
                "Core Banking",
                LocalDate.of(2026, 8, 30),
                "RISK-001",
                "Risk Committee",
                "Current value exceeds red threshold"
        );
    }

    private KriRequest updatedRequest() {
        return new KriRequest(
                "Loan Default Ratio",
                "Credit",
                "Treasury Director",
                "Retail Banking",
                "MONTHLY",
                "Tracks default exposure for the retail loan book.",
                "PERCENTAGE",
                new BigDecimal("5.0000"),
                "LOWER_IS_BETTER",
                new BigDecimal("4.0000"),
                new BigDecimal("6.0000"),
                new BigDecimal("8.0000"),
                new BigDecimal("8.5000"),
                "Core Banking",
                LocalDate.of(2026, 9, 15),
                "RISK-001",
                "Executive Risk Committee",
                "Current value exceeds red threshold"
        );
    }
}
