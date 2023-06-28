package com.matsinger.barofishserver.notification;

import com.matsinger.barofishserver.utils.fcm.FcmRequestDto;
import com.matsinger.barofishserver.utils.fcm.FcmService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final FcmService fcmService;

    public Page<Notification> selectNotificationListWithUserId(Integer userId, PageRequest pageRequest) {
        return notificationRepository.findAllByUserId(userId, pageRequest);
    }

    public Integer countAllNotificationByUserId(Integer userId) {
        return notificationRepository.countAllByUserId(userId);
    }

    public Notification addNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void sendFcmToUser(Integer userId, NotificationMessageType type, NotificationMessage message) {
        Notification
                notification =
                Notification.builder().userId(userId).type(message.getNotificationType(type)).title(message.getNotificationTitle(
                        type)).content(message.getMessage(type)).build();
        addNotification(notification);
        fcmService.sendFcmByToken(FcmRequestDto.builder().title(notification.getTitle()).body(notification.getContent()).targetUserId(
                userId).build());
    }
}
