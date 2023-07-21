package com.matsinger.barofishserver.notification.application;

import com.matsinger.barofishserver.notification.domain.Notification;
import com.matsinger.barofishserver.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.notification.repository.NotificationRepository;
import com.matsinger.barofishserver.utils.fcm.FcmRequestDto;
import com.matsinger.barofishserver.utils.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationCommandService {
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    public void addNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void sendFcmToUser(Integer userId, NotificationMessageType type, NotificationMessage message) {
        Notification
                notification =
                Notification.builder().userId(userId).type(message.getNotificationType(type)).title(message.getNotificationTitle(
                        type)).content(message.getMessage(type)).build();
        addNotification(notification);
        fcmService.sendFcmByToken(FcmRequestDto.builder().title(notification.getTitle()).body(notification.getContent().replaceAll(
                "<.+>",
                "")).targetUserId(userId).build());
    }
}
