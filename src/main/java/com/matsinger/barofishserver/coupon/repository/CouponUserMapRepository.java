package com.matsinger.barofishserver.coupon.repository;

import com.matsinger.barofishserver.coupon.domain.CouponUserMapId;
import com.matsinger.barofishserver.coupon.domain.CouponUserMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponUserMapRepository extends JpaRepository<CouponUserMap, CouponUserMapId> {

}
