package com.kyronic.riskengine.notifications.application.service;

import com.kyronic.riskengine.notifications.application.dto.NotificationEventRequest;
import com.kyronic.riskengine.notifications.domain.InAppNotification;
import com.kyronic.riskengine.notifications.domain.NotificationAuditHistory;
import com.kyronic.riskengine.notifications.domain.NotificationPriority;
import com.kyronic.riskengine.notifications.domain.NotificationState;
import com.kyronic.riskengine.notifications.domain.NotificationType;
import com.kyronic.riskengine.notifications.domain.ReadState;
import com.kyronic.riskengine.notifications.infrastructure.persistence.NotificationAuditHistoryRepository;
import com.kyronic.riskengine.notifications.infrastructure.persistence.NotificationRepository;
import com.kyronic.riskengine.notifications.interfaces.InvalidActionUrlException;
import com.kyronic.riskengine.notifications.interfaces.NotificationAccessDeniedException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Proxy;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NotificationServiceTest {

    @Test
    void kafkaEventCreatesNotification() {
        List<InAppNotification> notifications = new ArrayList<>();
        List<NotificationAuditHistory> audits = new ArrayList<>();
        NotificationService service = service(
                notifications,
                audits,
                new FixedCurrentUserProvider(actorUserId(), "system.admin"),
                new FixedReferenceGenerator("NTF-2026-00001"),
                new NotificationActionUrlFactory(),
                Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC)
        );

        var responses = service.createFromEvent(event(List.of(recipientUserId())));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).type()).isEqualTo(NotificationType.AUTHORIZATION_REQUIRED);
        assertThat(responses.get(0).actionUrl()).isEqualTo("/olts/incidents/OLTS-2026-04412/authorization");
        assertThat(notifications).hasSize(1);
    }

    @Test
    void duplicateEventDoesNotCreateDuplicateNotification() {
        List<InAppNotification> notifications = new ArrayList<>();
        List<NotificationAuditHistory> audits = new ArrayList<>();
        NotificationService service = service(
                notifications,
                audits,
                new FixedCurrentUserProvider(actorUserId(), "system.admin"),
                new FixedReferenceGenerator("NTF-2026-00001"),
                new NotificationActionUrlFactory(),
                Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC)
        );

        service.createFromEvent(event(List.of(recipientUserId())));
        service.createFromEvent(event(List.of(recipientUserId())));

        assertThat(notifications).hasSize(1);
    }

    @Test
    void userCannotReadAnotherUsersNotification() {
        List<InAppNotification> notifications = new ArrayList<>();
        notifications.add(existingNotification(recipientUserId(), UUID.randomUUID()));
        NotificationService service = service(
                notifications,
                new ArrayList<>(),
                new FixedCurrentUserProvider(999999L, "another.user"),
                new FixedReferenceGenerator("NTF-2026-00001"),
                new NotificationActionUrlFactory(),
                Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC)
        );

        assertThatThrownBy(() -> service.get(notifications.get(0).getId()))
                .isInstanceOf(NotificationAccessDeniedException.class);
    }

    @Test
    void markingAsReadRecordsReadAt() {
        List<InAppNotification> notifications = new ArrayList<>();
        InAppNotification notification = existingNotification(recipientUserId(), UUID.randomUUID());
        notifications.add(notification);
        NotificationService service = service(
                notifications,
                new ArrayList<>(),
                new FixedCurrentUserProvider(recipientUserId(), "dept.head"),
                new FixedReferenceGenerator("NTF-2026-00001"),
                new NotificationActionUrlFactory(),
                Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC)
        );

        var response = service.markRead(notification.getId());

        assertThat(response.readState()).isEqualTo(ReadState.READ);
        assertThat(response.readAt()).isEqualTo(Instant.parse("2026-08-06T08:30:00Z"));
    }

    @Test
    void archiveRemovesNotificationFromDefaultActiveList() {
        List<InAppNotification> notifications = new ArrayList<>();
        InAppNotification notification = existingNotification(recipientUserId(), UUID.randomUUID());
        notifications.add(notification);
        NotificationService service = service(
                notifications,
                new ArrayList<>(),
                new FixedCurrentUserProvider(recipientUserId(), "dept.head"),
                new FixedReferenceGenerator("NTF-2026-00001"),
                new NotificationActionUrlFactory(),
                Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC)
        );

        service.archive(notification.getId());

        var page = service.list(null, null, null, null, null, 0, 20);
        assertThat(page.getContent()).isEmpty();
    }

    @Test
    void unreadCountIsCorrect() {
        List<InAppNotification> notifications = new ArrayList<>();
        notifications.add(existingNotification(recipientUserId(), UUID.randomUUID()));
        notifications.add(existingNotification(recipientUserId(), UUID.randomUUID()));
        notifications.get(1).markRead(Instant.parse("2026-08-06T08:00:00Z"));
        NotificationService service = service(
                notifications,
                new ArrayList<>(),
                new FixedCurrentUserProvider(recipientUserId(), "dept.head"),
                new FixedReferenceGenerator("NTF-2026-00001"),
                new NotificationActionUrlFactory(),
                Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC)
        );

        var count = service.unreadCount();

        assertThat(count.unreadCount()).isEqualTo(1);
    }

    @Test
    void invalidActionUrlIsRejected() {
        List<InAppNotification> notifications = new ArrayList<>();
        NotificationService service = service(
                notifications,
                new ArrayList<>(),
                new FixedCurrentUserProvider(actorUserId(), "system.admin"),
                new FixedReferenceGenerator("NTF-2026-00001"),
                new NotificationActionUrlFactory() {
                    @Override
                    public String actionUrl(NotificationEventRequest event) {
                        validate("/external/invalid");
                        return "/external/invalid";
                    }
                },
                Clock.fixed(Instant.parse("2026-08-06T08:30:00Z"), ZoneOffset.UTC)
        );

        assertThatThrownBy(() -> service.createFromEvent(event(List.of(recipientUserId()))))
                .isInstanceOf(InvalidActionUrlException.class);
    }

    @Test
    void authServiceEventsResolveToAuthRoute() {
        NotificationActionUrlFactory factory = new NotificationActionUrlFactory();

        String actionUrl = factory.actionUrl(new NotificationEventRequest(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "auth.user.created.v1",
                "auth-service",
                "USER_ACCOUNT",
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "USR-2026-00001",
                List.of(recipientUserId()),
                null,
                NotificationType.SYSTEM,
                NotificationPriority.NORMAL,
                "User created",
                "A user was created.",
                Instant.parse("2026-08-06T08:30:00Z"),
                "corr-2"
        ));

        assertThat(actionUrl).isEqualTo("/auth/USR-2026-00001");
    }

    private NotificationService service(List<InAppNotification> notifications,
                                        List<NotificationAuditHistory> audits,
                                        NotificationCurrentUserProvider currentUserProvider,
                                        NotificationReferenceGenerator referenceGenerator,
                                        NotificationActionUrlFactory actionUrlFactory,
                                        Clock clock) {
        return new NotificationService(
                notificationRepository(notifications),
                notificationAuditRepository(audits),
                referenceGenerator,
                currentUserProvider,
                actionUrlFactory,
                new NotificationSseService(),
                clock
        );
    }

    @SuppressWarnings("unchecked")
    private NotificationRepository notificationRepository(List<InAppNotification> notifications) {
        return (NotificationRepository) Proxy.newProxyInstance(
                NotificationRepository.class.getClassLoader(),
                new Class<?>[]{NotificationRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        InAppNotification notification = (InAppNotification) args[0];
                        notifications.removeIf(existing -> existing.getId().equals(notification.getId()));
                        notifications.add(notification);
                        yield notification;
                    }
                    case "findByEventIdAndRecipientUserIdAndType" -> notifications.stream()
                            .filter(notification -> notification.getEventId().equals(args[0]))
                            .filter(notification -> notification.getRecipientUserId().equals(args[1]))
                            .filter(notification -> notification.getType() == args[2])
                            .findFirst();
                    case "findAllByRecipientUserId" -> notifications.stream()
                            .filter(notification -> notification.getRecipientUserId().equals(args[0]))
                            .sorted((left, right) -> right.getCreatedAt().compareTo(left.getCreatedAt()))
                            .toList();
                    case "findById" -> notifications.stream()
                            .filter(notification -> notification.getId().equals(args[0]))
                            .findFirst();
                    case "findAll" -> notifications;
                    case "delete" -> {
                        notifications.remove(args[0]);
                        yield null;
                    }
                    case "toString" -> "FakeNotificationRepository";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    @SuppressWarnings("unchecked")
    private NotificationAuditHistoryRepository notificationAuditRepository(List<NotificationAuditHistory> audits) {
        return (NotificationAuditHistoryRepository) Proxy.newProxyInstance(
                NotificationAuditHistoryRepository.class.getClassLoader(),
                new Class<?>[]{NotificationAuditHistoryRepository.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "save" -> {
                        NotificationAuditHistory auditHistory = (NotificationAuditHistory) args[0];
                        audits.add(auditHistory);
                        yield auditHistory;
                    }
                    case "toString" -> "FakeNotificationAuditHistoryRepository";
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    default -> throw new UnsupportedOperationException(method.getName());
                }
        );
    }

    private NotificationEventRequest event(List<Long> recipientUserIds) {
        return new NotificationEventRequest(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "authorization.requested.v1",
                "olts-service",
                "OLTS_INCIDENT",
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "OLTS-2026-04412",
                recipientUserIds,
                UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
                NotificationType.AUTHORIZATION_REQUIRED,
                NotificationPriority.HIGH,
                "OLTS authorization required",
                "OLTS-2026-04412 is awaiting your authorization.",
                Instant.parse("2026-08-06T08:30:00Z"),
                "corr-1"
        );
    }

    private InAppNotification existingNotification(Long recipientUserId, UUID id) {
        return new InAppNotification(
                id,
                "NTF-2026-00001",
                recipientUserId,
                NotificationType.AUTHORIZATION_REQUIRED,
                NotificationPriority.HIGH,
                "OLTS authorization required",
                "OLTS-2026-04412 is awaiting your authorization.",
                "olts-service",
                "OLTS_INCIDENT",
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "OLTS-2026-04412",
                UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc"),
                "/olts/incidents/OLTS-2026-04412/authorization",
                NotificationState.ACTIVE,
                ReadState.UNREAD,
                Instant.parse("2026-08-06T08:00:00Z"),
                null,
                null,
                null,
                Instant.parse("2027-02-06T08:00:00Z"),
                UUID.randomUUID(),
                "corr-1",
                0L
        );
    }

    private Long recipientUserId() {
        return 222222L;
    }

    private Long actorUserId() {
        return 333333L;
    }

    private static final class FixedCurrentUserProvider extends NotificationCurrentUserProvider {
        private final Long userId;
        private final String username;

        private FixedCurrentUserProvider(Long userId, String username) {
            this.userId = userId;
            this.username = username;
        }

        @Override
        public Long currentUserId() {
            return userId;
        }

        @Override
        public String currentUsername() {
            return username;
        }
    }

    private static final class FixedReferenceGenerator extends NotificationReferenceGenerator {
        private final String reference;

        private FixedReferenceGenerator(String reference) {
            super(null);
            this.reference = reference;
        }

        @Override
        public String nextReference() {
            return reference;
        }
    }
}
