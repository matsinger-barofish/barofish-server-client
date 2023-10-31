package com.matsinger.barofishserver.domain.settlement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class cancelSettleReq {
    Integer storeId;
    List<Integer> orderProductInfoIds;
    String cancelReason;
}