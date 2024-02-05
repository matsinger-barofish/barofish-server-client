package com.matsinger.barofishserver.domain.coupon.dto;

import com.matsinger.barofishserver.domain.coupon.domain.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CouponAddReq {
    private List<Integer> userIds;
    private String title;
    private CouponType type;
    private Integer amount;
    private Timestamp startAt;
    private Timestamp endAt;
    private Integer minPrice;
    private Integer maxPrice;
    private ExposureState exposureState; // 노출 상태
    private IssuanceState issuanceState; // 발급 상태
    private IssuanceType issuanceType; // 발급 형태
    private AppliedProduct appliedProduct; // 적용 상품
    private TobeIssued tobeIssued; // 발급 대상
    private Timestamp usageStart;
    private Timestamp usageEnd;
    private Integer periodOfUseAfterDownload; // 다운로드 이후 사용기간
    private String description;

    public Coupon toEntity() {
        return Coupon.builder()
                .title(title)
                .type(type)
                .amount(amount)
                .startAt(startAt)
                .endAt(endAt)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .exposureState(exposureState)
                .issuanceState(issuanceState)
                .issuanceType(issuanceType)
                .appliedProduct(appliedProduct)
                .tobeIssued(tobeIssued)
                .usageStart(usageStart)
                .usageEnd(usageEnd)
                .description(description)
                .build();
    }
}
