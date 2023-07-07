package com.matsinger.barofishserver.coupon;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    @Column(name = "min_price", nullable = false)
    private Integer minPrice = 0;
}
