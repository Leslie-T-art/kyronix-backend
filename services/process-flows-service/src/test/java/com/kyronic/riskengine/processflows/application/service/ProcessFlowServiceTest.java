package com.kyronic.riskengine.processflows.application.service;

import com.kyronic.riskengine.processflows.application.dto.ProcessFlowRequest;
import com.kyronic.riskengine.processflows.domain.ProcessFlowRecord;
import com.kyronic.riskengine.processflows.infrastructure.persistence.ProcessFlowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessFlowServiceTest {

    @Test
    void createGeneratesReference() {
        List<ProcessFlowRecord> storedRecords = new ArrayList<>();
        ProcessFlowService service = new ProcessFlowService(
                repository(storedRecords),
                new FixedGenerator("PF-2026-000001"),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.fixed(Instant.parse("2026-08-12T12:00:00Z"), ZoneOffset.UTC)
        );

        var response = service.create(request());

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.flowReference()).isEqualTo("PF-2026-000001");
    }

    @Test
    void countSupportsFilters() {
        List<ProcessFlowRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        ProcessFlowService service = new ProcessFlowService(
                repository(storedRecords),
                new FixedGenerator("PF-2026-000001"),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.systemUTC()
        );

        assertThat(service.count(4L, "ACTIVE")).isEqualTo(1L);
        assertThat(service.count(4L, "INACTIVE")).isZero();
    }

    @Test
    void listReturnsPage() {
        List<ProcessFlowRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        ProcessFlowService service = new ProcessFlowService(
                repository(storedRecords),
                new FixedGenerator("PF-2026-000001"),
                new FixedCurrentUserProvider("risk.inputter"),
                Clock.systemUTC()
        );

        var page = service.list(0, 20, "createdAt", "desc");

        assertThat(page.getContent()).hasSize(1);
    }

    @SuppressWarnings("unchecked")
    private ProcessFlowRepository repository(List<ProcessFlowRecord> storedRecords) {
        AtomicLong ids = new AtomicLong(storedRecords.size());
        return (ProcessFlowRepository) Proxy.newProxyInstance(
                ProcessFlowRepository.class.getClassLoader(),
                new Class<?>[]{ProcessFlowRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        ProcessFlowRecord record = (ProcessFlowRecord) args[0];
                        if (record.getId() == null) {
                            record = new ProcessFlowRecord(
                                    ids.incrementAndGet(),
                                    record.getFlowReference(),
                                    record.getName(),
                                    record.getDepartmentId(),
                                    record.getProcessOwner(),
                                    record.getStatus(),
                                    record.getDescription(),
                                    record.getCreatedAt(),
                                    record.getCreatedBy(),
                                    record.getUpdatedAt(),
                                    record.getUpdatedBy(),
                                    0L
                            );
                        }
                        ProcessFlowRecord finalRecord = record;
                        storedRecords.removeIf(existing -> existing.getId().equals(finalRecord.getId()));
                        storedRecords.add(finalRecord);
                        yield finalRecord;
                    }
                    case "findById" -> storedRecords.stream().filter(record -> record.getId().equals(args[0])).findFirst();
                    case "findAll" -> {
                        if (args != null && args.length == 1 && args[0] instanceof Pageable pageable) {
                            List<ProcessFlowRecord> content = storedRecords.stream().toList();
                            yield new PageImpl<>(content, pageable, content.size());
                        }
                        yield storedRecords.stream().toList();
                    }
                    case "count" -> (long) storedRecords.size();
                    case "countByDepartmentId" -> storedRecords.stream().filter(record -> record.getDepartmentId().equals(args[0])).count();
                    case "countByStatusIgnoreCase" -> storedRecords.stream().filter(record -> record.getStatus().equalsIgnoreCase((String) args[0])).count();
                    case "countByDepartmentIdAndStatusIgnoreCase" -> storedRecords.stream()
                            .filter(record -> record.getDepartmentId().equals(args[0]) && record.getStatus().equalsIgnoreCase((String) args[1]))
                            .count();
                    case "delete" -> {
                        ProcessFlowRecord record = (ProcessFlowRecord) args[0];
                        storedRecords.removeIf(existing -> existing.getId().equals(record.getId()));
                        yield null;
                    }
                    case "toString" -> "FakeProcessFlowRepository";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private ProcessFlowRequest request() {
        return new ProcessFlowRequest("Card Disputes", 4L, "Head of Operations", "ACTIVE", "Card dispute escalation flow");
    }

    private ProcessFlowRecord existingRecord() {
        return new ProcessFlowRecord(
                1L,
                "PF-2026-000001",
                "Card Disputes",
                4L,
                "Head of Operations",
                "ACTIVE",
                "Card dispute escalation flow",
                Instant.parse("2026-08-10T09:00:00Z"),
                "system.admin",
                Instant.parse("2026-08-10T09:00:00Z"),
                "system.admin",
                0L
        );
    }

    private static final class FixedGenerator extends ProcessFlowReferenceGenerator {
        private final String nextReference;

        private FixedGenerator(String nextReference) {
            super(null);
            this.nextReference = nextReference;
        }

        @Override
        public String nextReference() {
            return nextReference;
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
