package com.matsinger.barofishserver.coupon.repository;

import com.matsinger.barofishserver.coupon.domain.Coupon;
import com.matsinger.barofishserver.coupon.domain.CouponUserMapId;
import com.matsinger.barofishserver.coupon.domain.CouponUserMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUserMapRepository extends JpaRepository<CouponUserMap, CouponUserMapId> {
    List<CouponUserMap> findAllByCouponId(Integer couponId);


//    @Query(value = "SELECT c.*\n" +
//            "FROM coupon c \n" +
//            "WHERE c.id IN (SELECT coupon_id FROM coupon_user_map WHERE user_id = :userId) AND c.state = \'ACTIVE\' " +
//            "AND" +
//            " c.start_at < NOW( )\n" +
//            "  AND NOW( ) < c.end_at", nativeQuery = true)
    @Query(value =
            "SELECT c.*\n" +
                    "FROM coupon_user_map c \n" +
                    "WHERE c.id IN (SELECT coupon_id FROM coupon_user_map WHERE user_id = :userId) AND c.state = \'ACTIVE\' " +
                    "AND" +
                    " c.start_at < NOW( )\n" +
                    "  AND NOW( ) < c.end_at"
    , nativeQuery = true)
    List<CouponUserMap> selectDownloadedCoupon(@Param("userId") Integer userId);
}
