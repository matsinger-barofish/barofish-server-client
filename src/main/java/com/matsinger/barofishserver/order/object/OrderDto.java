package com.matsinger.barofishserver.order.object;

import com.matsinger.barofishserver.user.object.UserInfoDto;
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
    Integer totalAmount;
    Integer couponDiscount;
    Integer usePoint;
    Timestamp orderedAt;
    List<OrderProductDto> productInfos;
    OrderDeliverPlaceDto deliverPlace;
}
