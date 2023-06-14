package com.matsinger.barofishserver.notification;

import com.matsinger.barofishserver.banner.Banner;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;

    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<List<Notification>>> selectNotification(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<List<Notification>> res = new CustomResponse();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            List<Notification>
                    notifications =
                    notificationService.selectNotificationListWithUserId(tokenInfo.get().getId());
            res.setData(Optional.ofNullable(notifications));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

}
