package com.matsinger.barofishserver.domain.payment.portone.dto;

import lombok.Getter;

@Getter
public class PortOneAccessKeyResponse {
    private String code;
    private String message;
    private AccessKeyResponseBody response;
}
