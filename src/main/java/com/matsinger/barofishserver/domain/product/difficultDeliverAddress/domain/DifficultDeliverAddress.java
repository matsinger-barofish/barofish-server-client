package com.matsinger.barofishserver.domain.product.difficultDeliverAddress.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "difficult_deliver_address", schema = "barofish_dev", catalog = "")
public class DifficultDeliverAddress {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "product_id", nullable = false)
    private int productId;
    @Basic
    @Column(name = "bcode", nullable = false, length = 10)
    private String bcode;
}
