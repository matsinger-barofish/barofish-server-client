package com.matsinger.barofishserver.domain.notification.api;

import com.matsinger.barofishserver.domain.notification.application.NotificationQueryService;
import com.matsinger.barofishserver.domain.notification.domain.Notification;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<Page<Notification>>> selectNotification(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                 @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                 @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<Notification>> res = new CustomResponse<>();

                TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);

        PageRequest pageRequest = PageRequest.of(page, take, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification>
                notifications =
                notificationQueryService.selectNotificationListWithUserId(tokenInfo.getId(), pageRequest);
        res.setData(Optional.ofNullable(notifications));
        return ResponseEntity.ok(res);
    }
}
