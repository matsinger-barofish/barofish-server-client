package com.matsinger.barofishserver.coupon.application;

import com.matsinger.barofishserver.coupon.domain.Coupon;
import com.matsinger.barofishserver.coupon.domain.CouponUserMap;
import com.matsinger.barofishserver.coupon.domain.CouponUserMapId;
import com.matsinger.barofishserver.coupon.domain.UserCouponState;
import com.matsinger.barofishserver.coupon.repository.CouponRepository;
import com.matsinger.barofishserver.coupon.repository.CouponUserMapRepository;
import com.matsinger.barofishserver.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.domain.UserState;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.utils.Common;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    private final OrderProductInfoRepository infoRepository;
    private final Common utils;

    public void downloadCoupon(Integer userId, Integer couponId) {
        Coupon findCoupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰을 찾을 수 없습니다."));

        Integer expiryPeriod = findCoupon.getExpiryPeriod();
        LocalDateTime couponExpiryDate = LocalDateTime.now().plus(expiryPeriod, ChronoUnit.DAYS);

        mapRepository.save(CouponUserMap.builder()
                .couponId(couponId)
                .userId(userId)
                .isUsed(false)
                .expiryDate(Timestamp.valueOf(couponExpiryDate))
                .userCouponState(UserCouponState.ACTIVE)
                .build());
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

    public void sendCouponCreateNotification(Coupon coupon, List<Integer> userIds) {
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
        map.ifPresent(couponUserMap -> {
            couponUserMap.setIsUsed(true);
            mapRepository.save(couponUserMap);
        });
    }
    public void unUseCoupon(Integer couponId, Integer userId) {
        Optional<CouponUserMap> map = mapRepository.findById(new CouponUserMapId(userId, couponId));
        map.ifPresent(couponUserMap -> {
            couponUserMap.setIsUsed(false);
            mapRepository.save(couponUserMap);
        });
    }

    public void publishSystemCoupon(Integer userId) throws Exception {
        Tuple countData = infoRepository.countFinalConfirmedOrderWithUserId(userId);
        int count = Integer.parseInt(countData.get("count").toString());
        Integer couponId = null;
        if (count == 1) couponId = 1;
        else if (count == 2) couponId = 2;
        else if (count == 3) couponId = 3;
        if (couponId != null) {
            Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new Exception("쿠폰 정보를 찾을 수 없습니다."));
            CouponUserMap
                    couponUserMap =
                    CouponUserMap.builder().couponId(couponId).userId(userId).isUsed(false).build();
            mapRepository.save(couponUserMap);
        }
    }

    public void addCouponUserMapList(List<CouponUserMap> couponUserMaps) {
        mapRepository.saveAll(couponUserMaps);
    }
}
