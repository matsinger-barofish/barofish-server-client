package com.matsinger.barofishserver.coupon;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    @Column(name = "is_used", nullable = false)
    private Boolean isUsed;
}
