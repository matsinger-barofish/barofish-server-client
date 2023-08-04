package com.matsinger.barofishserver.coupon.domain;

import com.matsinger.barofishserver.coupon.dto.CouponDto;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "coupon", schema = "barofish_dev", catalog = "")
public class Coupon {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponState state;
    @Basic
    @Column(name = "public_type")
    @Enumerated(EnumType.STRING)
    private CouponPublicType publicType;
    @Basic
    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Basic
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType type;

    @Basic
    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Basic
    @Column(name = "start_at", nullable = false)
    private Timestamp startAt;

    @Basic
    @Column(name = "end_at", nullable = true)
    private Timestamp endAt; // 쿠폰 자체의 만료기간

    @Basic
    @Builder.Default
    @Column(name = "min_price", nullable = false)
    private Integer minPrice = 0;

    @Basic
    @Column(name = "expiry_period")
    private Integer expiryPeriod; // 쿠폰을 발급한 유저가 쿠폰을 쓰지 않으면 만료되는 기간

    public CouponDto convert2Dto(List<UserInfoDto> users) {
        return CouponDto.builder().id(this.getId()).title(this.getTitle()).state(this.getState()).publicType(this.getPublicType()).type(
                this.getType()).amount(this.getAmount()).startAt(this.getStartAt()).endAt(this.getEndAt()).minPrice(this.getMinPrice()).users(
                users).build();
    }

    public void setState(CouponState state) {
        this.state = state;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
