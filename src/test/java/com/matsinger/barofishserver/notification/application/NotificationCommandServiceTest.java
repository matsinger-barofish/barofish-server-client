package com.matsinger.barofishserver.notification.application;

import com.matsinger.barofishserver.domain.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.domain.notification.dto.NotificationMessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootTest
@ActiveProfiles("local")
@Transactional
class NotificationCommandServiceTest {

    @Autowired private NotificationCommandService notificationCommandService;

    @DisplayName("쿠폰을 발급 받았을 때 알림 메시지를 전송한다.")
    @Test
    void testMethodNameHere() {
        // given
        Integer userId = 10009;
        NotificationMessageType messageType = NotificationMessageType.COUPON_ARRIVED;
        NotificationMessage notificationMessage = NotificationMessage.builder()
                .productName("테스트입니다.")
                .orderedAt(Timestamp.valueOf(LocalDateTime.now()))
                .userName("태경")
                .couponName("test")
                .customContent("customContent")
                .isCanceledByRegion(false)
                .storeName("storeName")
                .build();

        notificationCommandService.sendFcmToUser(userId, messageType, notificationMessage);
        // when

        // then
    }
}