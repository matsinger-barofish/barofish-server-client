package com.matsinger.barofishserver.basketProduct.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "basket_product_option", schema = "barofish_dev", catalog = "")
public class BasketProductOption {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "order_product_id", nullable = false)
    private int orderProductId;
    @Basic
    @Column(name = "option_id", nullable = false)
    private int optionId;

}
