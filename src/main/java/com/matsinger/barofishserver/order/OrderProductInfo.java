package com.matsinger.barofishserver.order;

import com.matsinger.barofishserver.order.dto.request.OrderReqProductInfoDto;
import com.matsinger.barofishserver.order.dto.request.OrderReqProductOptionDto;
import com.matsinger.barofishserver.order.dto.response.OrderProductInfoDto;
import com.matsinger.barofishserver.order.dto.response.OrderProductOptionDto;
import com.matsinger.barofishserver.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "order_store_info_id", nullable = false)
    private OrderStoreInfo orderStoreInfo;

    public void setOrderStoreInfo(OrderStoreInfo orderStoreInfo) {
        this.orderStoreInfo = orderStoreInfo;
        orderStoreInfo.getOrderProductInfos().add(this);
    }

    @Basic
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private OrderState state;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "orderProductInfo")
    @Column(name = "order_product_option_id", nullable = false)
    private List<OrderProductOption> orderProductOptions = new ArrayList<>();

    @Basic
    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "discount_rate", nullable = false)
    private double discountRate;

    @Basic
    @Column(name = "delivery_fee", nullable = false)
    private int deliveryFee;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return getId() == that.getId() && getPrice() == that.getPrice() && Double.compare(that.getDiscountRate(), getDiscountRate()) == 0 && getDeliveryFee() == that.getDeliveryFee() && getOrderStoreInfo().equals(that.getOrderStoreInfo()) && getProduct().equals(that.getProduct()) && getState() == that.getState() && getOrderProductOptions().equals(that.getOrderProductOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOrderStoreInfo(), getProduct(), getState(), getOrderProductOptions(), getPrice(), getDiscountRate(), getDeliveryFee());
    }

    public OrderProductInfoDto toDto(List<OrderProductOptionDto> optionDtos) {
        return OrderProductInfoDto.builder()
                .productId(id)
                .originPrice(price)
                .discountRate(discountRate)
                .deliveryFee(deliveryFee)
                .state(state)
                .options(optionDtos).build();
    }
}
