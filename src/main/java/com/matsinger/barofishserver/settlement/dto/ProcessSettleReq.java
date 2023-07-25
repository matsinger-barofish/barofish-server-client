package com.matsinger.barofishserver.settlement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ProcessSettleReq {
    Integer storeId;
    List<Integer> orderProductInfoIds;
}
