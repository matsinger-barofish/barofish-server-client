package com.matsinger.barofishserver.domain.payment.portone.dto;

import lombok.Getter;

@Getter
public class AccessKeyResponseBody {
    private String access_token;
    private String now;
    private String expired_at;
}
