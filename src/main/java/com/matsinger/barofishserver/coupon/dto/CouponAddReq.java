package com.matsinger.barofishserver.coupon.dto;

import com.matsinger.barofishserver.coupon.domain.CouponType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
public class CouponAddReq {
    String title;
    CouponType type;
    Integer amount;
    Timestamp startAt;
    Timestamp endAt;
    Integer minPrice;
}
