package com.matsinger.barofishserver.coupon.application;

import com.matsinger.barofishserver.coupon.domain.Coupon;
import com.matsinger.barofishserver.coupon.domain.CouponUserMap;
import com.matsinger.barofishserver.coupon.domain.CouponUserMapId;
import com.matsinger.barofishserver.coupon.repository.CouponRepository;
import com.matsinger.barofishserver.coupon.repository.CouponUserMapRepository;
import com.matsinger.barofishserver.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.domain.UserState;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.utils.Common;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponCommandService {
    private final CouponUserMapRepository mapRepository;
    private final CouponRepository couponRepository;
    private final UserCommandService userService;
    private final NotificationCommandService notificationCommandService;
    private final Common utils;

    public void downloadCoupon(Integer userId, Integer couponId) {
        mapRepository.save(CouponUserMap.builder().couponId(couponId).userId(userId).isUsed(false).build());
    }

    public Coupon addCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void updateCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }

    public void deleteCoupon(Integer id) {
        couponRepository.deleteById(id);
    }

    public void sendCouponCreateNotification(Coupon coupon) {
        List<User> users = userService.selectUserWithState(UserState.ACTIVE);
        for (User user : users) {
            Optional<UserInfo> userInfo = userService.selectOptionalUserInfo(user.getId());
            if (userInfo.isPresent()) notificationCommandService.sendFcmToUser(user.getId(),
                    NotificationMessageType.COUPON_ARRIVED,
                    NotificationMessage.builder().couponName(coupon.getTitle()).userName(userInfo.get().getNickname()).build());
        }
    }

    public void useCoupon(Integer couponId, Integer userId) {
        Optional<CouponUserMap> map = mapRepository.findById(new CouponUserMapId(userId, couponId));
        map.ifPresent(couponUserMap -> couponUserMap.setIsUsed(true));
    }
}
