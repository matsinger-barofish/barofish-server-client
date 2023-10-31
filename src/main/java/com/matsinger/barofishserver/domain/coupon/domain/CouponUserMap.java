package com.matsinger.barofishserver.domain.coupon.domain;

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
}
