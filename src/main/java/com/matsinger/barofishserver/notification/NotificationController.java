package com.matsinger.barofishserver.notification;

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

    private final NotificationService notificationService;

    private final JwtService jwt;
    private final Common utils;

    @GetMapping("/")
    public ResponseEntity<CustomResponse<Page<Notification>>> selectNotification(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                                 @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                                 @RequestParam(value = "take", required = false, defaultValue = "10") Integer take) {
        CustomResponse<Page<Notification>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Notification>
                    notifications =
                    notificationService.selectNotificationListWithUserId(tokenInfo.get().getId(), pageRequest);
            res.setData(Optional.ofNullable(notifications));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

}
