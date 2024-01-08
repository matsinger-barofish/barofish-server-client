package com.matsinger.barofishserver.domain.coupon.domain;

import com.matsinger.barofishserver.domain.coupon.repository.CouponUserMapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("local")
class CouponUserMapTest {

    @Autowired private CouponUserMapRepository couponUserMapRepository;

    @DisplayName("")
    @Test
    void test() {
        // given
        Optional<CouponUserMap> byUserIdAndCouponId = couponUserMapRepository.findByUserIdAndCouponId(10000, 10000);
        // when

        // then
    }
}