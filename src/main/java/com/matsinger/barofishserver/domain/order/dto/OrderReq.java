package com.matsinger.barofishserver.domain.order.dto;

import com.matsinger.barofishserver.domain.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.domain.order.dto.request.VBankRefundInfoReq;
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
    private VBankRefundInfoReq vbankRefundInfo;
}
