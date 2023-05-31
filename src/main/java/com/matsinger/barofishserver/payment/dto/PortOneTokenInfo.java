package com.matsinger.barofishserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortOneTokenInfo {

    private String access_token;
    private int expired_at;
    private int now;
}
