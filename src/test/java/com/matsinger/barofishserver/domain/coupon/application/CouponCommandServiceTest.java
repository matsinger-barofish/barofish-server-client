package com.matsinger.barofishserver.domain.coupon.application;

import com.matsinger.barofishserver.domain.coupon.dto.CouponDeleteRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@SpringBootTest
@ActiveProfiles("local")
class CouponCommandServiceTest {
    @Autowired private CouponCommandService couponCommandService;

    @DisplayName("")
    @Test
    void test() {
        // given
        couponCommandService.deleteUserCoupon(
                new CouponDeleteRequest(10000, List.of(1,2,3)));
        // when

        // then
    }
}