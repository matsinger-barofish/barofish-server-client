package com.matsinger.barofishserver.order.orderprductinfo.dto;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductState;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderCancelReason;
import com.matsinger.barofishserver.product.domain.ProductDeliverFeeType;
import com.matsinger.barofishserver.product.optionitem.dto.OptionItemDto;
import com.matsinger.barofishserver.product.dto.ProductListDto;

import com.matsinger.barofishserver.store.domain.StoreDeliverFeeType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class OrderProductDto {
    private Integer id;
    private Integer storeId;
    private String storeProfile;
    private String storeName;
    private Integer deliverFee;
    private ProductDeliverFeeType deliverFeeType;
    private Integer minOrderPrice;
    private ProductListDto product;
    private OrderProductState state;
    private String optionName;
    private OptionItemDto optionItem;
    private Integer price;
    private Integer amount;
    private String deliverCompany;
    private String invoiceCode;
    private String deliverCompanyCode;
    private Timestamp finalConfirmedAt;
    private Boolean needTaxation;
    // private Integer deliveryFee;
    private OrderCancelReason cancelReason;
    private String cancelReasonContent;
    private Boolean isReviewWritten;
}
