package com.matsinger.barofishserver.utils.fcm;

import com.google.api.core.ApiFuture;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;
    private final FcmConfig fcmConfig;

    public void sendFcmByToken(FcmRequestDto requestDto) {
        List<FcmToken> tokens = fcmTokenRepository.findAllByUserId(requestDto.getTargetUserId());
        if (tokens != null && tokens.size() != 0) {
            for (FcmToken token : tokens) {
                Notification
                        notification =
                        Notification.builder().setTitle(requestDto.getTitle()).setBody(requestDto.getBody()).build();
                Message message = Message.builder().setToken(token.getToken()).setNotification(notification)
//                               .putAllData(requestDto.getData())
                                         .setAndroidConfig(AndroidConfig.builder().setNotification(AndroidNotification.builder().setTitle(
                                                 requestDto.getTitle()).setBody(requestDto.getBody()).setSound("default").setChannelId(
                                                 "barofish").build()).build()).setApnsConfig(ApnsConfig.builder().putHeader(
                                "apns-priority",
                                "5").setAps(Aps.builder().setSound("default").setContentAvailable(true).build()).build()).build();
                try {
//                    firebaseMessaging.send(message);
                    ApiFuture<String> response = fcmConfig.firebaseMessaging().sendAsync(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
