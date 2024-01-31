package com.matsinger.barofishserver.domain.payment.portone.dto;

import lombok.Getter;

@Getter
public class PortOneVbankHolderResponse {
    private String code;
    private String message;
    private PortOneVbankHolderResponseBody response;
}
