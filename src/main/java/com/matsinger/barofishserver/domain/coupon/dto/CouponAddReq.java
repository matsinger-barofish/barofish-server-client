package com.matsinger.barofishserver.domain.coupon.dto;

import com.matsinger.barofishserver.domain.coupon.domain.CouponType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CouponAddReq {
    List<Integer> userIds;
    String title;
    CouponType type;
    Integer amount;
    Timestamp startAt;
    Timestamp endAt;
    Integer minPrice;
}
