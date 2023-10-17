package com.matsinger.barofishserver.coupon.application;

import com.matsinger.barofishserver.coupon.domain.Coupon;
import com.matsinger.barofishserver.coupon.domain.CouponUserMap;
import com.matsinger.barofishserver.coupon.domain.CouponUserMapId;
import com.matsinger.barofishserver.coupon.repository.CouponRepository;
import com.matsinger.barofishserver.coupon.repository.CouponUserMapRepository;
import com.matsinger.barofishserver.notification.application.NotificationCommandService;
import com.matsinger.barofishserver.notification.dto.NotificationMessage;
import com.matsinger.barofishserver.notification.dto.NotificationMessageType;
import com.matsinger.barofishserver.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.store.repository.StoreScrapRepository;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.domain.UserState;
import com.matsinger.barofishserver.user.repository.UserRepository;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import com.matsinger.barofishserver.utils.Common;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponCommandService {
    private final CouponUserMapRepository couponUserMapRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final NotificationCommandService notificationCommandService;
    private final OrderProductInfoRepository infoRepository;
    private final Common utils;
    private final UserInfoRepository userInfoRepository;

    public void downloadCoupon(Integer userId, Integer couponId) {
        couponUserMapRepository.save(CouponUserMap.builder().couponId(couponId).userId(userId).isUsed(false).build());
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
        List<User>
                users =
                userIds == null ? userRepository.findAllByState(UserState.ACTIVE) : userRepository.findAllByIdIn(userIds);;
        for (User user : users) {
            Optional<UserInfo> userInfo = userInfoRepository.findByUserId(user.getId());
            if (userInfo.isPresent()) notificationCommandService.sendFcmToUser(user.getId(),
                    NotificationMessageType.COUPON_ARRIVED,
                    NotificationMessage.builder().couponName(coupon.getTitle()).userName(userInfo.get().getNickname()).build());
        }
    }

    public void useCoupon(Integer couponId, Integer userId) {
        Optional<CouponUserMap> map = couponUserMapRepository.findById(new CouponUserMapId(userId, couponId));
        map.ifPresent(couponUserMap -> {
            couponUserMap.setIsUsed(true);
            couponUserMapRepository.save(couponUserMap);
        });
    }

    public void unUseCoupon(Integer couponId, Integer userId) {
        Optional<CouponUserMap> map = couponUserMapRepository.findById(new CouponUserMapId(userId, couponId));
        map.ifPresent(couponUserMap -> {
            couponUserMap.setIsUsed(false);
            couponUserMapRepository.save(couponUserMap);
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
            couponUserMapRepository.save(couponUserMap);
        }
    }

    public void publishNewUserCoupon(Integer userId) {
        Coupon signUpCoupon = couponRepository.findById(7)
                .orElseThrow(() -> new IllegalArgumentException("회원가입 쿠폰을 찾을 수 없습니다."));

        if (couponUserMapRepository.findById(
                new CouponUserMapId(userId, signUpCoupon.getId())).isPresent()) {
            throw new IllegalArgumentException("이미 회원가입 쿠폰을 발급 받으셨습니다.");
        }
        CouponUserMap userSignUpCoupon = CouponUserMap.builder()
                .userId(userId)
                .couponId(signUpCoupon.getId())
                .isUsed(false)
                .build();

        couponUserMapRepository.save(userSignUpCoupon);
    }

    public void addCouponUserMapList(List<CouponUserMap> couponUserMaps) {
        couponUserMapRepository.saveAll(couponUserMaps);
    }
}
