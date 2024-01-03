package com.matsinger.barofishserver.domain.coupon.application;

import com.matsinger.barofishserver.domain.coupon.domain.Coupon;
import com.matsinger.barofishserver.domain.coupon.domain.CouponPublicType;
import com.matsinger.barofishserver.domain.coupon.domain.CouponUserMap;
import com.matsinger.barofishserver.domain.coupon.domain.CouponUserMapId;
import com.matsinger.barofishserver.domain.coupon.repository.CouponRepository;
import com.matsinger.barofishserver.domain.coupon.repository.CouponUserMapRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.utils.Common;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CouponQueryService {
    private final CouponUserMapRepository mapRepository;
    private final CouponRepository couponRepository;
    private final Common utils;

    public Page<Coupon> selectCouponListByAdmin(PageRequest pageRequest, Specification<Coupon> spec) {
        return couponRepository.findAll(spec, pageRequest);
    }

    public Coupon selectCoupon(Integer couponId) {
        return couponRepository.findById(couponId).orElseThrow(() -> {
            throw new BusinessException("쿠폰 정보를 찾을 수 없습니다.");
        });
    }

    public List<Coupon> selectCouponWithPublicType(CouponPublicType couponPublicType) {
        return couponRepository.findAllByPublicType(couponPublicType);
    }

    public void checkValidCoupon(Integer couponId, Integer userId) {
        CouponUserMap
                map =
                mapRepository.findById(CouponUserMapId.builder().couponId(couponId).userId(userId).build()).orElseThrow(
                        () -> {
                            throw new BusinessException("발급 받지 않은 쿠폰입니다.");
                        });
        if (map.getIsUsed()) throw new BusinessException("이미 사용한 쿠폰입니다.");
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> {
            throw new BusinessException("쿠폰 정보를 찾을 수 없습니다.");
        });
        Timestamp now = utils.now();
        if (coupon.getStartAt().after(now)) throw new BusinessException("사용 기한 전의 쿠폰입니다.");
        if (coupon.getEndAt() != null) {
            if (coupon.getEndAt().before(now)) throw new BusinessException("사용 기한이 만료되었습니다.");
        }
    }

    public Boolean checkHasCoupon(Integer couponId, Integer userId) {
        return mapRepository.existsById(CouponUserMapId.builder().couponId(couponId).userId(userId).build());
    }

    public Boolean checkUsedCoupon(Integer couponId, Integer userId) {
        CouponUserMap
                data =
                mapRepository.findById(CouponUserMapId.builder().couponId(couponId).userId(userId).build()).orElseThrow(
                        () -> {
                            throw new BusinessException("발급 받지 않은 쿠폰입니다.");
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

    public List<Integer> selectPublishedCouponUserIds(Integer couponId) {
        return mapRepository.findAllByCouponId(couponId).stream().map(CouponUserMap::getUserId).toList();
    }

    public Coupon findById(Integer couponId) {
        return couponRepository.findById(couponId)
                               .orElseThrow(() -> new BusinessException("쿠폰 정보를 찾을 수 없습니다."));
    }

    public List<Coupon> selectUserCouponList(Integer userId) {
        return couponRepository.selectUserCouponList(userId);
    }

    public Coupon validateCoupon(int couponId, int minOrderPrice) {
        Coupon coupon = findById(couponId);
        coupon.checkAvailablePrice(minOrderPrice);
        coupon.checkExpiration();

        return coupon;
    }
}
