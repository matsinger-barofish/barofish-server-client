package com.matsinger.barofishserver.domain.coupon.application;

import com.matsinger.barofishserver.domain.coupon.domain.CouponUserMap;
import com.matsinger.barofishserver.domain.coupon.repository.CouponUserMapRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponUserMapQueryService {

    private final CouponUserMapRepository couponUserMapRepository;


    public CouponUserMap findByUserIdAndCouponId(Integer userId, int couponId) {
        return couponUserMapRepository.findByUserIdAndCouponId(userId, couponId)
                .orElseThrow(() -> new BusinessException("쿠폰을 찾을 수 없습니다."));
    }
}
