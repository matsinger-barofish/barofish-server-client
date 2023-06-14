package com.matsinger.barofishserver.notification;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> selectNotificationListWithUserId(Integer userId) {
        return notificationRepository.findAllByUserId(userId);
    }
}
