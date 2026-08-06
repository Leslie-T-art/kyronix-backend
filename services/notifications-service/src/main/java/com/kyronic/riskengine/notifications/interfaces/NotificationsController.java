package com.kyronic.riskengine.notifications.interfaces;

import com.kyronic.riskengine.common.api.ApiResponse;
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
import com.kyronic.riskengine.notifications.infrastructure.configuration.NotificationsOpenApiConfiguration;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Notifications", description = "In-app notifications, unread counts, and real-time notification stream.")
@SecurityRequirement(name = NotificationsOpenApiConfiguration.BEARER_SCHEME)
public class NotificationsController {

    private final NotificationService notificationService;
    private final NotificationSseService notificationSseService;
    private final NotificationCurrentUserProvider currentUserProvider;

    public NotificationsController(NotificationService notificationService,
                                   NotificationSseService notificationSseService,
                                   NotificationCurrentUserProvider currentUserProvider) {
        this.notificationService = notificationService;
        this.notificationSseService = notificationSseService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/notifications")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List notifications", description = "Retrieve notifications for the authenticated user.")
    public ApiResponse<Page<NotificationResponse>> list(@RequestParam(name = "type", required = false) NotificationType type,
                                                        @RequestParam(name = "priority", required = false) NotificationPriority priority,
                                                        @RequestParam(name = "readState", required = false) ReadState readState,
                                                        @RequestParam(name = "state", required = false) NotificationState state,
                                                        @RequestParam(name = "sourceService", required = false) String sourceService,
                                                        @RequestParam(name = "page", defaultValue = "0") int page,
                                                        @RequestParam(name = "size", defaultValue = "20") int size) {
        return ApiResponse.success("Notifications retrieved successfully",
                notificationService.list(type, priority, readState, state, sourceService, page, size), null);
    }

    @GetMapping("/notifications/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get notification", description = "Retrieve a single notification for the authenticated recipient.")
    public ApiResponse<NotificationResponse> get(@PathVariable("notificationId") UUID notificationId) {
        return ApiResponse.success("Notification retrieved successfully", notificationService.get(notificationId), null);
    }

    @GetMapping("/notifications/unread-count")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get unread count", description = "Retrieve the unread notification count for the authenticated user.")
    public ApiResponse<UnreadCountResponse> unreadCount() {
        return ApiResponse.success("Unread count retrieved successfully", notificationService.unreadCount(), null);
    }

    @PatchMapping("/notifications/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<NotificationResponse> markRead(@PathVariable("notificationId") UUID notificationId) {
        return ApiResponse.success("Notification marked as read successfully", notificationService.markRead(notificationId), null);
    }

    @PatchMapping("/notifications/{notificationId}/unread")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<NotificationResponse> markUnread(@PathVariable("notificationId") UUID notificationId) {
        return ApiResponse.success("Notification marked as unread successfully", notificationService.markUnread(notificationId), null);
    }

    @PatchMapping("/notifications/{notificationId}/archive")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<NotificationResponse> archive(@PathVariable("notificationId") UUID notificationId) {
        return ApiResponse.success("Notification archived successfully", notificationService.archive(notificationId), null);
    }

    @PatchMapping("/notifications/{notificationId}/dismiss")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<NotificationResponse> dismiss(@PathVariable("notificationId") UUID notificationId) {
        return ApiResponse.success("Notification dismissed successfully", notificationService.dismiss(notificationId), null);
    }

    @PostMapping("/notifications/read-all")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> readAll() {
        notificationService.readAll();
        return ApiResponse.success("Notifications marked as read successfully", null, null);
    }

    @PostMapping("/notifications/archive-all-read")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> archiveAllRead() {
        notificationService.archiveAllRead();
        return ApiResponse.success("Read notifications archived successfully", null, null);
    }

    @DeleteMapping("/notifications/{notificationId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> delete(@PathVariable("notificationId") UUID notificationId) {
        notificationService.delete(notificationId);
        return ApiResponse.success("Notification deleted successfully", null, null);
    }

    @GetMapping(path = "/notifications/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Open notification stream", description = "Open an SSE stream for real-time notification delivery to the authenticated user.")
    public SseEmitter stream() {
        return notificationSseService.subscribe(currentUserProvider.currentUserId());
    }

    @PostMapping("/internal/notifications/events")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create notifications from internal event", description = "Authenticated internal endpoint for creating notifications from a domain event payload.")
    public ApiResponse<List<NotificationResponse>> createFromEvent(@Valid @RequestBody NotificationEventRequest event) {
        return ApiResponse.success("Notifications created successfully", notificationService.createFromEvent(event), null);
    }
}
