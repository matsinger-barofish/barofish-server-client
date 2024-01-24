package com.matsinger.barofishserver.domain.payment.portone.api;

import lombok.Getter;

@Getter
public class AccountCheckRequest {

    private String bankCode;
    private String bankNum;
    private String holderName;
}
