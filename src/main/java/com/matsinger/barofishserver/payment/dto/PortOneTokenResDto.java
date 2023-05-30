package com.matsinger.barofishserver.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortOneTokenResDto {

    private int code;
    private String message;

    private PortOneTokenInfo response;

}
