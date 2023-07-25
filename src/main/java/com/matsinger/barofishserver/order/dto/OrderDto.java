package com.matsinger.barofishserver.order.dto;

import com.matsinger.barofishserver.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.order.orderprductinfo.dto.OrderProductDto;
import com.matsinger.barofishserver.order.domain.OrderState;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
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
    Integer usePoint;
    Timestamp orderedAt;
    Boolean needTaxation;
    List<OrderProductDto> productInfos;
    OrderDeliverPlaceDto deliverPlace;
}
