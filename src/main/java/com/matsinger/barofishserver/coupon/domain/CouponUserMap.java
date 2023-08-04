package com.matsinger.barofishserver.coupon.domain;

import io.opencensus.metrics.export.TimeSeries;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

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

    @Basic
    @Column(name = "expiry_date")
    private Timestamp expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_state", nullable = false)
    private UserCouponState userCouponState = UserCouponState.ACTIVE;

    public boolean isExpired() {
        boolean isExpired = this.expiryDate.before(expiryDate);
        if (isExpired) {
            this.userCouponState = UserCouponState.INACTIVE;
        }
        return isExpired;
    }
    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }
}
