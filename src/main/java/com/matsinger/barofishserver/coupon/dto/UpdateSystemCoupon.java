package com.matsinger.barofishserver.coupon.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateSystemCoupon {
    Integer order_1stAmount;
    Integer order_3rdAmount;
    Integer order_5thAmount;
}
