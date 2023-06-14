package com.matsinger.barofishserver.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    List<Coupon> findAllByState(CouponState state);

    @Query(value = "SELECT *\n" +
            "FROM coupon c\n" +
            "WHERE id NOT IN (SELECT user_id FROM coupon_user_map WHERE user_id = :userId) AND c.state = \'ACTIVE\' AND c.start_at < NOW( )\n" +
            "  AND NOW( ) < c.end_at", nativeQuery = true)
    List<Coupon> selectNotDownloadCoupon(@Param("userId") Integer userId);

    @Query(value = "SELECT c.*\n" +
            "FROM coupon c \n" +
            "WHERE id IN (SELECT user_id FROM coupon_user_map WHERE user_id = :userId) AND c.state = \'ACTIVE\' AND c.start_at < NOW( )\n" +
            "  AND NOW( ) < c.end_at", nativeQuery = true)
    List<Coupon> selectDownloadedCoupon(@Param("userId") Integer userId);

    @Query(value = "SELECT c.*\n" +
            "FROM coupon c\n" +
            "WHERE id IN (SELECT user_id FROM coupon_user_map cum WHERE cum.user_id = :userId AND cum.is_used=FALSE )" +
            " AND c.state = \'ACTIVE\' AND c.start_at < NOW( )\n" +
            "  AND NOW( ) < c.end_at", nativeQuery = true)
    List<Coupon> selectCanUseCoupon(@Param("userId") Integer userId);

}
