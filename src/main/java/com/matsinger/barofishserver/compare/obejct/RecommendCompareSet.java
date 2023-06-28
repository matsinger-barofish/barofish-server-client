package com.matsinger.barofishserver.compare.obejct;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendCompareSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private RecommendCompareSetType type;

    @Basic
    @Column(name = "product1_id", nullable = false)
    private Integer product1Id;

    @Basic
    @Column(name = "product2_id", nullable = false)
    private Integer product2Id;

    @Basic
    @Column(name = "product3_id", nullable = false)
    private Integer product3Id;
}
