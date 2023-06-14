package com.matsinger.barofishserver.coupon;

import com.matsinger.barofishserver.utils.Common;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponService {
    private final CouponUserMapRepository mapRepository;
    private final CouponRepository couponRepository;
    private final Common utils;

    public List<Coupon> selectCouponListByAdmin() {
        return couponRepository.findAllByState(CouponState.ACTIVE);
    }

    public Coupon selectCoupon(Integer couponId) {
        return couponRepository.findById(couponId).orElseThrow(() -> {
            throw new Error("쿠폰 정보를 찾을 수 없습니다.");
        });
    }

    public Boolean checkValidCoupon(Integer couponId, Integer userId) {
        CouponUserMap
                map =
                mapRepository.findById(CouponUserMapId.builder().couponId(couponId).userId(userId).build()).orElseThrow(
                        () -> {
                            throw new Error("발급 받지 않은 쿠폰입니다.");
                        });
        if (map.getIsUsed()) throw new Error("이미 사용한 쿠폰입니다.");
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> {
            throw new Error("쿠폰 정보를 찾을 수 없습니다.");
        });
        Timestamp now = utils.now();
        if (coupon.getStartAt().after(now)) throw new Error("사용 기한 전의 쿠폰입니다.");
        if (coupon.getEndAt().before(now)) throw new Error("사용 기한이 만료되었습니다.");
        return true;
    }

    public Boolean checkHasCoupon(Integer couponId, Integer userId) {
        return mapRepository.existsById(CouponUserMapId.builder().couponId(couponId).userId(userId).build());
    }

    public Boolean checkUsedCoupon(Integer couponId, Integer userId) {
        CouponUserMap
                data =
                mapRepository.findById(CouponUserMapId.builder().couponId(couponId).userId(userId).build()).orElseThrow(
                        () -> {
                            throw new Error("발급 받지 않은 쿠폰입니다.");
                        });
        return data.getIsUsed();
    }

    public List<Coupon> selectNotDownloadCoupon(Integer userId) {
        return couponRepository.selectNotDownloadCoupon(userId);
    }

    public List<Coupon> selectDownloadedCoupon(Integer userId) {
        return couponRepository.selectDownloadedCoupon(userId);
    }

    public List<Coupon> selectCanUseCoupon(Integer userId) {
        return couponRepository.selectCanUseCoupon(userId);
    }

    public void downloadCoupon(Integer userId, Integer couponId) {
        mapRepository.save(CouponUserMap.builder().couponId(couponId).userId(userId).isUsed(false).build());
    }

    public Coupon addCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public Coupon updateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }

    public void deleteCoupon(Integer id) {
        couponRepository.deleteById(id);
    }

}
