package com.matsinger.barofishserver.basketProduct.obejct;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderProductId() {
        return orderProductId;
    }

    public void setOrderProductId(int orderProductId) {
        this.orderProductId = orderProductId;
    }

    public int getOptionId() {
        return optionId;
    }

    public void setOptionId(int optionId) {
        this.optionId = optionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasketProductOption that = (BasketProductOption) o;
        return id == that.id && orderProductId == that.orderProductId && optionId == that.optionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderProductId, optionId);
    }
}
