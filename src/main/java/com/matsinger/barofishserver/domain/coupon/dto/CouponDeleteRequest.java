package com.matsinger.barofishserver.domain.coupon.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class CouponDeleteRequest {
    private final Integer userId;
    private final List<Integer> couponIds;
}
