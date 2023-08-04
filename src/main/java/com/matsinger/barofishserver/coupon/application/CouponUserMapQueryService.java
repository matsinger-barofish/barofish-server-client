package com.matsinger.barofishserver.coupon.application;

import com.matsinger.barofishserver.coupon.domain.CouponUserMap;
import com.matsinger.barofishserver.coupon.repository.CouponUserMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponUserMapQueryService {

    private final CouponUserMapRepository couponUserMapRepository;

    public List<CouponUserMap> selectDownloadedUserCoupon(int userId) {
        couponUserMapRepository.selectDownloadedCoupon(userId);
    }
}
