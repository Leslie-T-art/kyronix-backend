package com.kyronic.riskengine.processflows.application.service;

import com.kyronic.riskengine.processflows.application.dto.ProcessFlowRequest;
import com.kyronic.riskengine.processflows.domain.ProcessFlowRecord;
import com.kyronic.riskengine.processflows.domain.ProcessFlowWorkflowStatus;
import com.kyronic.riskengine.processflows.infrastructure.persistence.ProcessFlowRepository;
import com.kyronic.riskengine.processflows.infrastructure.storage.MinioProcessFlowDocumentStorage;
import com.kyronic.riskengine.processflows.infrastructure.storage.StoredDocument;
import com.kyronic.riskengine.processflows.interfaces.ProcessFlowConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProcessFlowServiceTest {

    @Test
    void createGeneratesReference() {
        List<ProcessFlowRecord> storedRecords = new ArrayList<>();
        ProcessFlowService service = new ProcessFlowService(
                repository(storedRecords),
                new FixedGenerator("PF-2026-000001"),
                new FixedCurrentUserProvider(1001L, 4L, "risk.inputter"),
                new NoOpNotificationPublisher(),
                new FixedStorage(),
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
                new FixedCurrentUserProvider(1001L, 4L, "risk.inputter"),
                new NoOpNotificationPublisher(),
                new FixedStorage(),
                Clock.systemUTC()
        );

        assertThat(service.count(4L, "APPROVED")).isEqualTo(1L);
        assertThat(service.count(4L, "RETURNED")).isZero();
    }

    @Test
    void listReturnsPage() {
        List<ProcessFlowRecord> storedRecords = new ArrayList<>();
        storedRecords.add(existingRecord());
        ProcessFlowService service = new ProcessFlowService(
                repository(storedRecords),
                new FixedGenerator("PF-2026-000001"),
                new FixedCurrentUserProvider(1001L, 4L, "risk.inputter"),
                new NoOpNotificationPublisher(),
                new FixedStorage(),
                Clock.systemUTC()
        );

        var page = service.list(0, 20, "createdAt", "desc");

        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void inputterCannotApproveOwnProcessFlow() {
        List<ProcessFlowRecord> storedRecords = new ArrayList<>();
        storedRecords.add(pendingApprovalRecord());
        ProcessFlowService service = new ProcessFlowService(
                repository(storedRecords),
                new FixedGenerator("PF-2026-000001"),
                new FixedCurrentUserProvider(1001L, 4L, "risk.inputter", "INPUTTER"),
                new NoOpNotificationPublisher(),
                new FixedStorage(),
                Clock.systemUTC()
        );

        assertThatThrownBy(() -> service.approve(1L, "approved"))
                .isInstanceOf(ProcessFlowConflictException.class)
                .hasMessageContaining("inputter cannot authorize");
    }

    @Test
    void authorizerCanApprovePendingProcessFlow() {
        List<ProcessFlowRecord> storedRecords = new ArrayList<>();
        storedRecords.add(pendingApprovalRecord());
        ProcessFlowService service = new ProcessFlowService(
                repository(storedRecords),
                new FixedGenerator("PF-2026-000001"),
                new FixedCurrentUserProvider(1002L, 4L, "dept.head", "AUTHORIZER"),
                new NoOpNotificationPublisher(),
                new FixedStorage(),
                Clock.systemUTC()
        );

        var response = service.approve(1L, "approved");

        assertThat(response.workflowStatus()).isEqualTo(ProcessFlowWorkflowStatus.APPROVED);
        assertThat(response.authorizerUsername()).isEqualTo("dept.head");
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
                            record.getProcessFlowName(),
                            record.getDepartmentId(),
                            record.getProcessOwner(),
                            record.getDescription(),
                            record.getValidFromDate(),
                            record.getValidToDate(),
                                    record.getWorkflowStatus(),
                                    record.getOriginalFileName(),
                                    record.getContentType(),
                                    record.getFileSize(),
                                    record.getBucketName(),
                                    record.getObjectKey(),
                                    record.getInputterUserId(),
                                    record.getInputterUsername(),
                                    record.getAuthorizerUserId(),
                                    record.getAuthorizerUsername(),
                                    record.getAuthorizerComment(),
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
                    case "countByWorkflowStatus" -> storedRecords.stream().filter(record -> record.getWorkflowStatus().equals(args[0])).count();
                    case "countByDepartmentIdAndWorkflowStatus" -> storedRecords.stream()
                            .filter(record -> record.getDepartmentId().equals(args[0]) && record.getWorkflowStatus().equals(args[1]))
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
        ProcessFlowRequest request = new ProcessFlowRequest();
        request.setProcessFlowName("Card Disputes");
        request.setDepartmentId(4L);
        request.setDescription("Card dispute escalation flow");
        request.setValidFromDate(LocalDate.of(2026, 8, 1));
        request.setValidToDate(LocalDate.of(2026, 12, 31));
        request.setDocument(new MockMultipartFile("document", "flow.pdf", "application/pdf", "pdf".getBytes()));
        return request;
    }

    private ProcessFlowRecord existingRecord() {
        return new ProcessFlowRecord(
                1L,
                "PF-2026-000001",
                "Card Disputes",
                4L,
                "risk.inputter",
                "Card dispute escalation flow",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 12, 31),
                ProcessFlowWorkflowStatus.APPROVED,
                "flow.pdf",
                "application/pdf",
                3L,
                "process-flows-dept-4",
                "PF-2026-000001/flow.pdf",
                1001L,
                "risk.inputter",
                1002L,
                "dept.head",
                "approved",
                Instant.parse("2026-08-10T09:00:00Z"),
                "system.admin",
                Instant.parse("2026-08-10T09:00:00Z"),
                "system.admin",
                0L
        );
    }

    private ProcessFlowRecord pendingApprovalRecord() {
        return new ProcessFlowRecord(
                1L,
                "PF-2026-000001",
                "Card Disputes",
                4L,
                "risk.inputter",
                "Card dispute escalation flow",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 12, 31),
                ProcessFlowWorkflowStatus.PENDING_APPROVAL,
                "flow.pdf",
                "application/pdf",
                3L,
                "process-flows-dept-4",
                "PF-2026-000001/flow.pdf",
                1001L,
                "risk.inputter",
                null,
                null,
                null,
                Instant.parse("2026-08-10T09:00:00Z"),
                "risk.inputter",
                Instant.parse("2026-08-10T09:00:00Z"),
                "risk.inputter",
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

    private static final class NoOpNotificationPublisher implements ProcessFlowNotificationPublisher {
        @Override
        public void publishApproved(ProcessFlowRecord record) {
        }

        @Override
        public void publishRejected(ProcessFlowRecord record) {
        }

        @Override
        public void publishReturned(ProcessFlowRecord record) {
        }
    }

    private static final class FixedCurrentUserProvider extends CurrentUserProvider {
        private final Long userId;
        private final Long departmentId;
        private final String username;
        private final String[] roles;

        private FixedCurrentUserProvider(Long userId, Long departmentId, String username, String... roles) {
            this.userId = userId;
            this.departmentId = departmentId;
            this.username = username;
            this.roles = roles;
        }

        @Override
        public Long currentDepartmentId() {
            return departmentId;
        }

        @Override
        public String currentUsername() {
            return username;
        }

        @Override
        public Long currentUserId() {
            return userId;
        }

        @Override
        public boolean hasAnyRole(String... expectedRoles) {
            for (String role : roles) {
                for (String expectedRole : expectedRoles) {
                    if (role.equals(expectedRole)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static final class FixedStorage extends MinioProcessFlowDocumentStorage {
        private FixedStorage() {
            super(null);
        }

        @Override
        public StoredDocument store(Long departmentId, String flowReference, org.springframework.web.multipart.MultipartFile multipartFile) {
            return new StoredDocument(multipartFile.getOriginalFilename(), multipartFile.getContentType(), multipartFile.getSize(), "process-flows-dept-" + departmentId, flowReference + "/" + multipartFile.getOriginalFilename());
        }

        @Override
        public byte[] read(String bucketName, String objectKey) {
            return "pdf".getBytes();
        }

        @Override
        public void delete(String bucketName, String objectKey) {
        }
    }
}
