package com.matsinger.barofishserver.domain.coupon.domain;

import com.matsinger.barofishserver.global.exception.BusinessException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@IdClass(value = CouponUserMapId.class)
@Table(name = "coupon_user_map", schema = "barofish_dev", catalog = "")
public class CouponUserMap {

    @Id
    @Column(name = "user_id", nullable = false)
    private int userId;

    @Id
    @Column(name = "coupon_id", nullable = false)
    private int couponId;

    @Basic
    @Column(name = "is_used", nullable = false, columnDefinition = "TINYINT")
    private Boolean isUsed;

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public void checkIsUsed() {
        if (isUsed == true) {
            throw new BusinessException("이미 사용한 쿠폰입니다.");
        }
    }

    public void useCoupon() {
        if (isUsed == true) {
            throw new BusinessException("이미 사용한 쿠폰입니다.");
        }
        this.isUsed = true;
    }
}
