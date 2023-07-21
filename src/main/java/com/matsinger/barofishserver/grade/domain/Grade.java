package com.matsinger.barofishserver.grade.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "grade", schema = "barofish_dev", catalog = "")
public class Grade {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Basic
    @Column(name = "point_rate")
    private int pointRate;
    @Basic
    @Column(name = "min_order_price", nullable = false)
    private Integer minOrderPrice;
    @Basic
    @Column(name = "min_order_count", nullable = false)
    private Integer minOrderCount;
}
