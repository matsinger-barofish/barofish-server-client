package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.order.dto.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.OrderProductOptionDto;
import com.matsinger.barofishserver.order.dto.response.OrderResponseDto;
import com.matsinger.barofishserver.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.aspectj.weaver.ast.Or;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// TODO: 파트너마다 묶음배송이 되기 때문에 개별적인 상품에 배송 관련 state가 있어야 함.
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "order_product_info", schema = "barofish_dev", catalog = "")
public class OrderProductInfo {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public void setOrder(Order order) {
        this.order = order;
        order.getOrderProductInfo().add(this);
    }

    @Basic
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private OrderState state;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orderProductInfo")
    @Column(name = "order_product_option_id", nullable = false)
    private List<OrderProductOption> orderProductOption = new ArrayList<>();

    public void setOrderProductOption(OrderProductOption option) {
        orderProductOption.add(option);
    }

    @Basic
    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "discount_rate", nullable = false)
    private double discountRate;

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

    public Order getOrder() {
        return this.order;
    }

    public int getProductId() {
        return product.getId();
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
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
        OrderProductInfo that = (OrderProductInfo) o;
        return id == that.id &&
                product.getId() == that.product.getId() &&
                price == that.price &&
                amount == that.amount &&
                deliveryFee == that.deliveryFee &&
                Objects.equals(order.getId(), that.order.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order.getId(), product.getId(), price, amount, deliveryFee);
    }

    public OrderProductInfoDto toDto(List<OrderProductOptionDto> options) {
        return OrderProductInfoDto.builder()
                .productId(this.id)
                .originPrice(this.price)
                .discountRate(this.discountRate)
                .amount(this.amount)
                .state(this.state)
                .options(options)
                .deliveryFee(this.deliveryFee).build();
    }
}
