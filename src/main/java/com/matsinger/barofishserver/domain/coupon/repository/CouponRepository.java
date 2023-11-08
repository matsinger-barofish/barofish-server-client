package com.matsinger.barofishserver.domain.coupon.repository;

import com.matsinger.barofishserver.domain.coupon.domain.Coupon;
import com.matsinger.barofishserver.domain.coupon.domain.CouponPublicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Integer>, JpaSpecificationExecutor<Coupon> {

//    Page<Coupon> findAllByState(CouponState state, Specification<Coupon> spec, PageRequest pageRequest);

    @Query(value = "SELECT *\n" +
            "FROM coupon c\n" +
            "WHERE c.id NOT IN (SELECT coupon_id FROM coupon_user_map WHERE user_id = :userId) AND c.state = " +
            "\'ACTIVE\'\n" +
            " AND c.public_type = \'PUBLIC\'\n" +
            " AND c.start_at < NOW( )\n" +
            "  AND NOW( ) < c.end_at", nativeQuery = true)
    List<Coupon> selectNotDownloadCoupon(@Param("userId") Integer userId);

    @Query(value = "SELECT c.*\n" +
            "FROM coupon c \n" +
            "WHERE c.id IN (SELECT coupon_id FROM coupon_user_map WHERE user_id = :userId AND is_used = FALSE) AND c" +
            ".state = " +
            "\'ACTIVE\' " +
            "AND" +
            " c.start_at < NOW( )\n" +
            "  AND (NOW( ) < c.end_at OR c.end_at is null)", nativeQuery = true)
    List<Coupon> selectDownloadedCoupon(@Param("userId") Integer userId);

    @Query(value = "SELECT c.*\n" +
            "FROM coupon c\n" +
            "WHERE id IN (SELECT coupon_id FROM coupon_user_map cum WHERE cum.user_id = :userId AND cum.is_used=FALSE )" +
            " AND c.state = \'ACTIVE\' AND c.start_at < NOW( )\n" +
            "  AND NOW( ) < c.end_at", nativeQuery = true)
    List<Coupon> selectCanUseCoupon(@Param("userId") Integer userId);

    List<Coupon> findAllByPublicType(CouponPublicType type);

    @Query(value = "SELECT c.* FROM coupon c " +
            "WHERE id IN (SELECT coupon_id FROM coupon_user_map cum where cum.user_id = :userId)",
            nativeQuery = true)
    List<Coupon> selectUserCouponList(@Param("userId") Integer userId);
}
