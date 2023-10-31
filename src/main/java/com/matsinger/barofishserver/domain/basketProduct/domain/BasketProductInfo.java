package com.matsinger.barofishserver.domain.basketProduct.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "basket_product_info", schema = "barofish_dev", catalog = "")
public class BasketProductInfo {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic
    @Column(name = "product_id", nullable = false)
    private int productId;
    @Basic
    @Column(name = "amount", nullable = false)
    private int amount;
    @Basic
    @Column(name = "delivery_fee", nullable = false)
    private int deliveryFee;

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
