package com.matsinger.barofishserver.domain.coupon.application;

import com.matsinger.barofishserver.domain.coupon.domain.CouponUserMap;
import com.matsinger.barofishserver.domain.coupon.repository.CouponUserMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponUserMapCommandService {

    private final CouponUserMapRepository couponUserMapRepository;
    private final CouponUserMapQueryService couponUserMapQueryService;

    public void useCoupon(Integer userId, Integer couponId) {
        CouponUserMap findedCouponUserMap = couponUserMapQueryService
                .findByUserIdAndCouponId(userId, couponId);
        findedCouponUserMap.checkIsUsed();
        findedCouponUserMap.setIsUsed(true);
    }
}
