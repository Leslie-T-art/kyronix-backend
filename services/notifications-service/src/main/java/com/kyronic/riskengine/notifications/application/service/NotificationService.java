package com.kyronic.riskengine.notifications.application.service;

import com.kyronic.riskengine.notifications.application.dto.NotificationEventRequest;
import com.kyronic.riskengine.notifications.application.dto.NotificationResponse;
import com.kyronic.riskengine.notifications.application.dto.UnreadCountResponse;
import com.kyronic.riskengine.notifications.domain.InAppNotification;
import com.kyronic.riskengine.notifications.domain.NotificationAuditHistory;
import com.kyronic.riskengine.notifications.domain.NotificationPriority;
import com.kyronic.riskengine.notifications.domain.NotificationState;
import com.kyronic.riskengine.notifications.domain.NotificationType;
import com.kyronic.riskengine.notifications.domain.ReadState;
import com.kyronic.riskengine.notifications.infrastructure.persistence.NotificationAuditHistoryRepository;
import com.kyronic.riskengine.notifications.infrastructure.persistence.NotificationRepository;
import com.kyronic.riskengine.notifications.interfaces.NotificationAccessDeniedException;
import com.kyronic.riskengine.notifications.interfaces.NotificationNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationAuditHistoryRepository auditHistoryRepository;
    private final NotificationReferenceGenerator referenceGenerator;
    private final NotificationCurrentUserProvider currentUserProvider;
    private final NotificationActionUrlFactory actionUrlFactory;
    private final NotificationSseService notificationSseService;
    private final Clock clock;

    public NotificationService(NotificationRepository notificationRepository,
                               NotificationAuditHistoryRepository auditHistoryRepository,
                               NotificationReferenceGenerator referenceGenerator,
                               NotificationCurrentUserProvider currentUserProvider,
                               NotificationActionUrlFactory actionUrlFactory,
                               NotificationSseService notificationSseService,
                               Clock clock) {
        this.notificationRepository = notificationRepository;
        this.auditHistoryRepository = auditHistoryRepository;
        this.referenceGenerator = referenceGenerator;
        this.currentUserProvider = currentUserProvider;
        this.actionUrlFactory = actionUrlFactory;
        this.notificationSseService = notificationSseService;
        this.clock = clock;
    }

    public List<NotificationResponse> createFromEvent(NotificationEventRequest event) {
        Instant occurredAt = event.occurredAt() == null ? Instant.now(clock) : event.occurredAt();
        return event.recipientUserIds().stream()
                .map(recipientUserId -> createRecipientNotification(event, recipientUserId, occurredAt))
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<NotificationResponse> list(NotificationType type,
                                           NotificationPriority priority,
                                           ReadState readState,
                                           NotificationState state,
                                           String sourceService,
                                           int page,
                                           int size) {
        Long currentUserId = requireCurrentUserId();
        List<NotificationResponse> filtered = notificationRepository.findAllByRecipientUserId(currentUserId, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .filter(notification -> !notification.isExpired(Instant.now(clock)))
                .filter(notification -> type == null || notification.getType() == type)
                .filter(notification -> priority == null || notification.getPriority() == priority)
                .filter(notification -> readState == null || notification.getReadState() == readState)
                .filter(notification -> state == null ? notification.getState() == NotificationState.ACTIVE : notification.getState() == state)
                .filter(notification -> sourceService == null || notification.getSourceService().equalsIgnoreCase(sourceService))
                .map(this::toResponse)
                .toList();
        Pageable pageable = PageRequest.of(page, size);
        int start = Math.min((int) pageable.getOffset(), filtered.size());
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        return new PageImpl<>(filtered.subList(start, end), pageable, filtered.size());
    }

    @Transactional(readOnly = true)
    public NotificationResponse get(UUID notificationId) {
        return toResponse(getOwned(notificationId));
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse unreadCount() {
        Long currentUserId = requireCurrentUserId();
        long count = notificationRepository.findAllByRecipientUserId(currentUserId, Sort.unsorted()).stream()
                .filter(notification -> notification.getState() == NotificationState.ACTIVE)
                .filter(notification -> notification.getReadState() == ReadState.UNREAD)
                .filter(notification -> !notification.isExpired(Instant.now(clock)))
                .count();
        return new UnreadCountResponse(count);
    }

    public NotificationResponse markRead(UUID notificationId) {
        InAppNotification notification = getOwned(notificationId);
        notification.markRead(Instant.now(clock));
        InAppNotification saved = notificationRepository.save(notification);
        audit(saved, "READ");
        return toResponse(saved);
    }

    public NotificationResponse markUnread(UUID notificationId) {
        InAppNotification notification = getOwned(notificationId);
        notification.markUnread();
        InAppNotification saved = notificationRepository.save(notification);
        audit(saved, "UNREAD");
        return toResponse(saved);
    }

    public NotificationResponse archive(UUID notificationId) {
        InAppNotification notification = getOwned(notificationId);
        notification.archive(Instant.now(clock));
        InAppNotification saved = notificationRepository.save(notification);
        audit(saved, "ARCHIVED");
        return toResponse(saved);
    }

    public NotificationResponse dismiss(UUID notificationId) {
        InAppNotification notification = getOwned(notificationId);
        notification.dismiss(Instant.now(clock));
        InAppNotification saved = notificationRepository.save(notification);
        audit(saved, "DISMISSED");
        return toResponse(saved);
    }

    public void readAll() {
        Long currentUserId = requireCurrentUserId();
        Instant now = Instant.now(clock);
        notificationRepository.findAllByRecipientUserId(currentUserId, Sort.unsorted()).stream()
                .filter(notification -> notification.getState() == NotificationState.ACTIVE)
                .filter(notification -> notification.getReadState() == ReadState.UNREAD)
                .forEach(notification -> {
                    notification.markRead(now);
                    notificationRepository.save(notification);
                    audit(notification, "READ_ALL");
                });
    }

    public void archiveAllRead() {
        Long currentUserId = requireCurrentUserId();
        Instant now = Instant.now(clock);
        notificationRepository.findAllByRecipientUserId(currentUserId, Sort.unsorted()).stream()
                .filter(notification -> notification.getReadState() == ReadState.READ)
                .filter(notification -> notification.getState() == NotificationState.ACTIVE)
                .forEach(notification -> {
                    notification.archive(now);
                    notificationRepository.save(notification);
                    audit(notification, "ARCHIVE_ALL_READ");
                });
    }

    public void delete(UUID notificationId) {
        InAppNotification notification = getOwned(notificationId);
        audit(notification, "DELETED");
        notificationRepository.delete(notification);
    }

    @Scheduled(fixedDelay = 300000)
    public void expireNotifications() {
        Instant now = Instant.now(clock);
        notificationRepository.findAll(Sort.by(Sort.Direction.ASC, "createdAt")).stream()
                .filter(notification -> notification.getState() == NotificationState.ACTIVE)
                .filter(notification -> notification.isExpired(now))
                .forEach(notification -> {
                    notification.expire();
                    notificationRepository.save(notification);
                    audit(notification, "EXPIRED");
                });
    }

    private NotificationResponse createRecipientNotification(NotificationEventRequest event, Long recipientUserId, Instant occurredAt) {
        return notificationRepository.findByEventIdAndRecipientUserIdAndType(event.eventId(), recipientUserId, event.type())
                .map(this::toResponse)
                .orElseGet(() -> {
                    String actionUrl = actionUrlFactory.actionUrl(event);
                    InAppNotification notification = new InAppNotification(
                            UUID.randomUUID(),
                            referenceGenerator.nextReference(),
                            recipientUserId,
                            event.type(),
                            event.priority(),
                            sanitize(event.title()),
                            sanitize(event.message()),
                            event.sourceService(),
                            event.entityType(),
                            event.entityId(),
                            event.businessReference(),
                            event.departmentId(),
                            actionUrl,
                            NotificationState.ACTIVE,
                            ReadState.UNREAD,
                            occurredAt,
                            null,
                            null,
                            null,
                            occurredAt.plusSeconds(60L * 60 * 24 * 180),
                            event.eventId(),
                            event.correlationId(),
                            null
                    );
                    InAppNotification saved = notificationRepository.save(notification);
                    audit(saved, "CREATED");
                    NotificationResponse response = toResponse(saved);
                    notificationSseService.publishToUser(recipientUserId, response);
                    return response;
                });
    }

    private InAppNotification getOwned(UUID notificationId) {
        Long currentUserId = requireCurrentUserId();
        InAppNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        if (!notification.getRecipientUserId().equals(currentUserId)) {
            throw new NotificationAccessDeniedException(notificationId);
        }
        return notification;
    }

    private Long requireCurrentUserId() {
        Long currentUserId = currentUserProvider.currentUserId();
        if (currentUserId == null) {
            throw new NotificationAccessDeniedException(null);
        }
        return currentUserId;
    }

    private void audit(InAppNotification notification, String action) {
        auditHistoryRepository.save(new NotificationAuditHistory(
                UUID.randomUUID(),
                notification.getId(),
                action,
                currentUserProvider.currentUserId(),
                currentUserProvider.currentUsername(),
                Instant.now(clock),
                notification.getCorrelationId()
        ));
    }

    private String sanitize(String value) {
        return value.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "").trim();
    }

    private NotificationResponse toResponse(InAppNotification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getNotificationReference(),
                notification.getType(),
                notification.getPriority(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getSourceService(),
                notification.getEntityType(),
                notification.getEntityId(),
                notification.getBusinessReference(),
                notification.getDepartmentId(),
                notification.getActionUrl(),
                notification.getState(),
                notification.getReadState(),
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.getArchivedAt(),
                notification.getDismissedAt(),
                notification.getExpiresAt(),
                notification.getEventId(),
                notification.getCorrelationId()
        );
    }
}
