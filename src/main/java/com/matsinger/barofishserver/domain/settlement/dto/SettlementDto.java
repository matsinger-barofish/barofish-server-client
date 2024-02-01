package com.matsinger.barofishserver.domain.settlement.dto;

import com.matsinger.barofishserver.domain.settlement.domain.SettlementState;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
public class SettlementDto {
    private int id;
    // 필요 Store 정보 추가
    private int storeId;
    private String storeName;
    private SettlementState state;
    private int settlementAmount;
    private Timestamp settledAt;
    private String cancelReason;

}
