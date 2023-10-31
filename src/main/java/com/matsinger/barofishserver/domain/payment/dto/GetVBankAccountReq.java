package com.matsinger.barofishserver.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GetVBankAccountReq {
    String orderId;
    Integer price;
    public String vBankCode;
    public Integer vBankDue;
    public String vBankHolder;
}
