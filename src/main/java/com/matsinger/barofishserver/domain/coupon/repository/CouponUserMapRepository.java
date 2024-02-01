package com.matsinger.barofishserver.domain.coupon.repository;

import com.matsinger.barofishserver.domain.coupon.domain.CouponUserMapId;
import com.matsinger.barofishserver.domain.coupon.domain.CouponUserMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponUserMapRepository extends JpaRepository<CouponUserMap, CouponUserMapId> {
    List<CouponUserMap> findAllByCouponId(Integer couponId);

    void deleteAllByUserIdIn(List<Integer> userIds);

    Optional<CouponUserMap> findByUserIdAndCouponId(int userId, int couponId);

    List<CouponUserMap> findByUserIdAndCouponIdIn(Integer userId, List<Integer> couponIds);
}
