package com.matsinger.barofishserver.order.dto;

import com.matsinger.barofishserver.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.order.orderprductinfo.dto.OrderProductDto;
import com.matsinger.barofishserver.order.domain.OrderState;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
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
    Integer totalAmount;
    Integer couponDiscount;
    String couponName;
    Integer usePoint;
    Timestamp orderedAt;
    Boolean needTaxation;
    String bankHolder;
    String bankCode;
    String bankAccount;
    String bankName;
    List<OrderProductDto> productInfos;
    OrderDeliverPlaceDto deliverPlace;
}
