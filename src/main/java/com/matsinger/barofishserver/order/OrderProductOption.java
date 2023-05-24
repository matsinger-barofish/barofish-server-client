package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.order.dto.OrderProductOptionDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_product_option", schema = "barofish_dev", catalog = "")
public class OrderProductOption {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Basic
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_product_id", nullable = false)
    private OrderProductInfo orderProductInfo;
    @Basic
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    @Basic
    @Column(name = "price", nullable = false)
    private int price;

    @Basic
    @Column(name = "amount", nullable = false)
    private int amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderProductId() {
        return orderProductInfo.getProductId();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderProductOption that = (OrderProductOption) o;
        return id == that.id &&
                orderProductInfo.getProductId() == that.getOrderProductId() &&
                price == that.price &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderProductInfo.getProductId(), name, price);
    }

    public OrderProductOptionDto toDto() {
        return OrderProductOptionDto.builder()
                .optionId(this.id)
                .optionName(this.name)
                .amount(this.amount)
                .optionPrice(this.price).build();
    }
}
