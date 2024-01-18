package com.matsinger.barofishserver.log.order_product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_product_log")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderProductLog {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_product_id", nullable = false)
    private String orderProductId;

    @Column(name = "state_before", nullable = false)
    private String stateBefore;

    @Column(name = "state_after", nullable = false)
    private String stateAfter;

    @Column(name = "memo", nullable = false)
    private String memo;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
}
