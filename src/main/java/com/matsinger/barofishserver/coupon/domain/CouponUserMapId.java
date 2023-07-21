package com.matsinger.barofishserver.coupon.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponUserMapId  implements Serializable {
    private Integer userId;
    private Integer couponId;
}
