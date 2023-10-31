package com.matsinger.barofishserver.domain.order.dto;

import com.matsinger.barofishserver.domain.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.domain.order.domain.OrderState;
import com.matsinger.barofishserver.domain.order.orderprductinfo.dto.OrderProductDto;
import com.matsinger.barofishserver.domain.userinfo.dto.UserInfoDto;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    String id;
    UserInfoDto user;
    OrderState state;
    String ordererName;
    String ordererTel;
    OrderPaymentWay paymentWay;
    Integer originTotalPrice;
    Integer totalAmount;
    Integer couponDiscount;
    String couponName;
    Integer usePoint;
    Timestamp orderedAt;
    // Boolean needTaxation;
    String bankHolder;
    String bankCode;
    String bankAccount;
    String bankName;
    List<OrderProductDto> productInfos;
    OrderDeliverPlaceDto deliverPlace;
}
