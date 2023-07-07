package com.matsinger.barofishserver.jwt;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Jwt {
    private String accessToken;
    private String refreshToken;
}
