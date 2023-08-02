package com.matsinger.barofishserver.order.dto;

import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderCancelReason;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestChangeProduct {
    private OrderCancelReason cancelReason;
    private String reasonContent;
}
