package com.matsinger.barofishserver.domain.payment.portone.dto;

import lombok.Getter;

@Getter
public class AccountCheckRequest {

    private Integer bankCodeId;
    private String bankNum;
    private String holderName;
}
