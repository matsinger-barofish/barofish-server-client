package com.matsinger.barofishserver.basketProduct;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketProductInfo that = (BasketProductInfo) o;
        return id == that.id &&
                userId == that.userId &&
                productId == that.productId &&
                amount == that.amount &&
                deliveryFee == that.deliveryFee;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, productId, amount, deliveryFee);
    }
}
