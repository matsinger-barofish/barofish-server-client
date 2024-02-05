package com.matsinger.barofishserver.domain.coupon.api;

import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.coupon.domain.CouponType;
import com.matsinger.barofishserver.domain.coupon.dto.CouponAddReq;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/coupon")
public class CouponControllerV2 {

    private final JwtService jwt;
    private final Common utils;
    private final CouponCommandService couponCommandService;

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Boolean>> addCouponV2(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                            @RequestPart(value = "data") CouponAddReq data) {

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);

        validateCouponAddRequest(data);
        Integer adminId = tokenInfo.getId();

        couponCommandService.registerCoupon(data, adminId);

        CustomResponse<Boolean> res = new CustomResponse<>();
        res.setData(Optional.ofNullable(true));
        return ResponseEntity.ok(res);
    }



    private void validateCouponAddRequest(CouponAddReq data) {
        utils.validateString(data.getTitle(), 100L, "제목");
        if (data.getType() == null) {
            throw new BusinessException("할인 유형을 입력해주세요.");
        }
        if (data.getType().equals(CouponType.RATE)) {
            if (data.getAmount() > 100) {
                throw new BusinessException("할인율은 100%를 넘을 수 없습니다.");
            }
            if (data.getMaxPrice() == null) {
                throw new BusinessException("최대 할인금액을 입력해주세요.");
            }
        }
        if (data.getType().equals(CouponType.AMOUNT)){
            if (data.getAmount() > data.getMinPrice())
                throw new BusinessException("할인 금액이 주문 최소 금액을 넘을 수 없습니다.");
        }
        if (data.getAmount() < 0) {
            throw new BusinessException("할인율을 확인해주세요.");
        }
        if (data.getMinPrice() == null) {
            data.setMinPrice(0);
        }
        if (data.getStartAt() == null){ throw new BusinessException("사용 가능 시작 기간을 입력해주세요.");}
        if (data.getExposureState() == null) {
            throw new BusinessException("노출 상태를 입력해주세요.");
        }
        if (data.getIssuanceState() == null) {
            throw new BusinessException("발급 상태를 입력해주세요.");
        }
        if (data.getIssuanceType() == null) {
            throw new BusinessException("발급 유형을 입력해주세요.");
        }
        if (data.getAppliedProduct() == null) {
            throw new BusinessException("적용 타입을 입력해주세요.");
        }
        if (data.getTobeIssued() == null) {
            throw new BusinessException("발급 대상을 입력해주세요.");
        }
        if (data.getUsageStart() == null || data.getUsageEnd() == null) {
            throw new BusinessException("사용기간을 입력해주세요.");
        }
    }
}
