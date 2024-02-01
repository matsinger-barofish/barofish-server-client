package com.matsinger.barofishserver.domain.coupon.domain;

import com.matsinger.barofishserver.domain.coupon.dto.CouponDto;
import com.matsinger.barofishserver.domain.userinfo.dto.UserInfoDto;
import com.matsinger.barofishserver.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    private Timestamp endAt;

    @Basic
    @Builder.Default
    @Column(name = "min_price", nullable = false)
    private Integer minPrice = 0;

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

    public void isAvailable(int price) {
        if (price < minPrice) {
            throw new BusinessException("쿠폰 최소 금액에 맞지 않습니다.");
        }

        if (startAt != null && endAt != null) {
            if (startAt.after(Timestamp.valueOf(LocalDateTime.now()))) {
                throw new BusinessException("사용기한 전의 쿠폰입니다.");
            }
            if (endAt.before(Timestamp.valueOf(LocalDateTime.now()))) {
                throw new BusinessException("쿠폰 사용 기한이 만료되었습니다.");
            }
        }
    }

    public void checkExpiration() {
        if (startAt.after(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new BusinessException("사용기한 전의 쿠폰입니다.");
        }
        if (startAt.before(Timestamp.valueOf(LocalDateTime.now()))) {
            throw new BusinessException("사용 기한이 만료되었습니다.");
        }
    }
}
