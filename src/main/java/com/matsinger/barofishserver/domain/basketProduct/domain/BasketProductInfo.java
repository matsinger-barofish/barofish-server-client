package com.matsinger.barofishserver.domain.basketProduct.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Column(name = "store_id", nullable = false)
    private int storeId;
    @Basic
    @Column(name = "product_id", nullable = false)
    private int productId;
    @Basic
    @Column(name = "option_id", nullable = false)
    private int optionId;
    @Column(name = "is_needed", nullable = false)
    private boolean isNeeded;
    @Basic
    @Column(name = "option_item_id", nullable = false)
    private int optionItemId;
    @Basic
    @Column(name = "amount", nullable = false)
    private int amount;
    @Basic
    @Column(name = "delivery_fee", nullable = false)
    private Integer deliveryFee;

    public void setAmount(int amount) {
        this.amount = amount;
    }


    public void addQuantity(Integer amount) {
        this.amount += amount;
    }
}
