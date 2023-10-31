package com.matsinger.barofishserver.domain.coupon.repository;

import com.matsinger.barofishserver.domain.coupon.domain.CouponUserMapId;
import com.matsinger.barofishserver.domain.coupon.domain.CouponUserMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CouponUserMapRepository extends JpaRepository<CouponUserMap, CouponUserMapId> {
    List<CouponUserMap> findAllByCouponId(Integer couponId);

    void deleteAllByUserIdIn(List<Integer> userIds);
}
