package com.matsinger.barofishserver.order.object;

import com.matsinger.barofishserver.user.object.UserInfo;
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
    UserInfo user;
    OrderState state;
    Integer totalAmount;
    Timestamp orderedAt;
    List<OrderProductDto> productInfos;
    OrderDeliverPlace deliverPlace;
}
