package com.matsinger.barofishserver.domain.coupon.dto;

import com.matsinger.barofishserver.domain.coupon.domain.CouponPublicType;
import com.matsinger.barofishserver.domain.coupon.domain.CouponState;
import com.matsinger.barofishserver.domain.coupon.domain.CouponType;
import com.matsinger.barofishserver.domain.userinfo.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDto {
    private int id;
    private CouponState state;
    private CouponPublicType publicType;
    private String title;
    private CouponType type;
    private Integer amount;
    private Timestamp startAt;
    private Timestamp endAt;
    private Integer minPrice;
    private List<UserInfoDto> users;
}
