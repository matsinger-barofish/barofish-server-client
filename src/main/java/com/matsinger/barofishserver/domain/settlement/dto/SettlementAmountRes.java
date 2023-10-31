package com.matsinger.barofishserver.domain.settlement.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementAmountRes {
    Integer settledAmount;
    Integer needSettleAmount;
}
