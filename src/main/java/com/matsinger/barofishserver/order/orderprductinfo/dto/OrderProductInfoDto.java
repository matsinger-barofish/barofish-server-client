package com.matsinger.barofishserver.order.orderprductinfo.dto;


import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderCancelReason;
import com.matsinger.barofishserver.product.dto.ProductListDto;

import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class OrderProductInfoDto {
    private int id;
    private String orderId;
    private int productId;
    private int optionItemId;
    private OrderProductState state;
    private Integer settlePrice;
    private int price;
    private int amount;
    private int deliveryFee;
    private OrderCancelReason cancelReason;
    private String cancelReasonContent;
    private String deliverCompanyCode;
    private String invoiceCode;
    private Boolean isSettled;
    private Timestamp settledAt;
    ProductListDto product;

}
