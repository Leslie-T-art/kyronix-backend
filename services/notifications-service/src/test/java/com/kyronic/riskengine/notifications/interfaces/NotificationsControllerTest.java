package com.kyronic.riskengine.notifications.interfaces;

import com.kyronic.riskengine.notifications.application.dto.NotificationEventRequest;
import com.kyronic.riskengine.notifications.application.dto.NotificationResponse;
import com.kyronic.riskengine.notifications.application.dto.UnreadCountResponse;
import com.kyronic.riskengine.notifications.application.service.NotificationCurrentUserProvider;
import com.kyronic.riskengine.notifications.application.service.NotificationService;
import com.kyronic.riskengine.notifications.application.service.NotificationSseService;
import com.kyronic.riskengine.notifications.domain.NotificationPriority;
import com.kyronic.riskengine.notifications.domain.NotificationState;
import com.kyronic.riskengine.notifications.domain.NotificationType;
import com.kyronic.riskengine.notifications.domain.ReadState;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationsControllerTest {

    @Test
    void unreadCountReturnsApiResponse() {
        NotificationsController controller = new NotificationsController(
                new FixedNotificationService(),
                new NotificationSseService(),
                new FixedCurrentUserProvider()
        );

        var response = controller.unreadCount();

        assertThat(response.success()).isTrue();
        assertThat(response.data().unreadCount()).isEqualTo(3);
    }

    @Test
    void listReturnsPagedNotifications() {
        NotificationsController controller = new NotificationsController(
                new FixedNotificationService(),
                new NotificationSseService(),
                new FixedCurrentUserProvider()
        );

        var response = controller.list(null, null, null, null, null, 0, 20);

        assertThat(response.success()).isTrue();
        assertThat(response.data().getContent()).hasSize(1);
    }

    private static final class FixedNotificationService extends NotificationService {

        private FixedNotificationService() {
            super(null, null, null, null, null, null, null);
        }

        @Override
        public org.springframework.data.domain.Page<NotificationResponse> list(NotificationType type,
                                                                               NotificationPriority priority,
                                                                               ReadState readState,
                                                                               NotificationState state,
                                                                               String sourceService,
                                                                               int page,
                                                                               int size) {
            return new PageImpl<>(List.of(notification()));
        }

        @Override
        public UnreadCountResponse unreadCount() {
            return new UnreadCountResponse(3);
        }

        @Override
        public NotificationResponse get(UUID notificationId) {
            return notification();
        }

        @Override
        public NotificationResponse markRead(UUID notificationId) {
            return notification();
        }

        @Override
        public NotificationResponse markUnread(UUID notificationId) {
            return notification();
        }

        @Override
        public NotificationResponse archive(UUID notificationId) {
            return notification();
        }

        @Override
        public NotificationResponse dismiss(UUID notificationId) {
            return notification();
        }

        @Override
        public List<NotificationResponse> createFromEvent(NotificationEventRequest event) {
            return List.of(notification());
        }

        private NotificationResponse notification() {
            return new NotificationResponse(
                    UUID.randomUUID(),
                    "NTF-2026-00001",
                    NotificationType.AUTHORIZATION_REQUIRED,
                    NotificationPriority.HIGH,
                    "OLTS authorization required",
                    "OLTS-2026-04412 is awaiting your authorization.",
                    "olts-service",
                    "OLTS_INCIDENT",
                    UUID.randomUUID(),
                    "OLTS-2026-04412",
                    UUID.randomUUID(),
                    "/olts/incidents/OLTS-2026-04412/authorization",
                    NotificationState.ACTIVE,
                    ReadState.UNREAD,
                    Instant.parse("2026-08-06T08:30:00Z"),
                    null,
                    null,
                    null,
                    Instant.parse("2027-02-06T08:30:00Z"),
                    UUID.randomUUID(),
                    "corr-1"
            );
        }
    }

    private static final class FixedCurrentUserProvider extends NotificationCurrentUserProvider {
        @Override
        public UUID currentUserId() {
            return UUID.fromString("22222222-2222-2222-2222-222222222222");
        }
    }
}
