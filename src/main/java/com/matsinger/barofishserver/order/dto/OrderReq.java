package com.matsinger.barofishserver.order.dto;

import com.matsinger.barofishserver.order.domain.OrderPaymentWay;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OrderReq {
    private String name;
    private String tel;
    private Integer couponId;
    private OrderPaymentWay paymentWay;
    private Integer point;
    private Integer totalPrice;
    private Integer couponDiscountPrice;
    private List<OrderProductReq> products;
    private Integer taxFreeAmount;
    private Integer deliverPlaceId;
    private Integer paymentMethodId;
}
