package com.matsinger.barofishserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortOneTokenReqDto {

    private String imp_key;
    private String imp_secret;
}
