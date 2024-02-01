package com.matsinger.barofishserver.domain.order.dto;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderCancelReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class RequestCancelReq {
    private OrderCancelReason cancelReason;
    private String content;
}
