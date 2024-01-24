package com.matsinger.barofishserver.domain.payment.portone.dto;

import lombok.Getter;

@Getter
public class PortOneAccessKeyRequest {

    private String imp_key;
    private String imp_secret;

    public PortOneAccessKeyRequest(String accessKey, String secretKey) {
        this.imp_key = accessKey;
        this.imp_secret = secretKey;
    }
}
