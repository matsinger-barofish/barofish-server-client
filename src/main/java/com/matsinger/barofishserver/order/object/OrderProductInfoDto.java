package com.matsinger.barofishserver.order.object;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.object.ProductListDto;
import jakarta.persistence.*;
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
