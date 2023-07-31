package com.matsinger.barofishserver.order.orderprductinfo.dto;


import com.matsinger.barofishserver.order.dto.OrderDto;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderCancelReason;
import com.matsinger.barofishserver.product.dto.ProductListDto;

import com.matsinger.barofishserver.store.domain.StoreDeliverFeeType;
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
    private com.matsinger.barofishserver.product.optionitem.dto.OptionItemDto optionItem;
    private OrderProductState state;
    private Integer settlePrice;
    private int price;
    private int amount;
    private int deliveryFee;
    private StoreDeliverFeeType deliverFeeType;
    private OrderCancelReason cancelReason;
    private String cancelReasonContent;
    private String deliverCompany;
    private String deliverCompanyCode;
    private String invoiceCode;
    private Boolean isSettled;
    private Timestamp settledAt;
    private Timestamp finalConfirmedAt;
    ProductListDto product;
    private OrderDto order;
    private Integer settlementRate;
}
