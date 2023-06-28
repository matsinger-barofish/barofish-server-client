package com.matsinger.barofishserver.utils.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.matsinger.barofishserver.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FcmService {
    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenRepository fcmTokenRepository;

    public String sendFcmByToken(FcmRequestDto requestDto) {
        List<FcmToken> tokens = fcmTokenRepository.findAllByUserId(requestDto.getTargetUserId());
        if (tokens != null && tokens.size() != 0) {
            for (FcmToken token : tokens) {
                Notification
                        notification =
                        Notification.builder().setTitle(requestDto.getTitle()).setBody(requestDto.getBody()).build();
                Message
                        message =
                        Message.builder().setToken(token.getToken()).setNotification(notification).putAllData(requestDto.getData()).setAndroidConfig(
                                AndroidConfig.builder().setNotification(AndroidNotification.builder().setTitle(
                                        requestDto.getTitle()).setBody(requestDto.getBody()).setSound("default").setChannelId(
                                        "barofish").build()).build()).setApnsConfig(ApnsConfig.builder().putHeader(
                                "apns-priority",
                                "5").setAps(Aps.builder().setSound("default").setContentAvailable(true).build()).build()).build();
                try {
                    firebaseMessaging.send(message);
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                    return "[FCM 전송 실패] userID : " + token.getUserId();
                }
            }
        }
        return "";
    }
}
