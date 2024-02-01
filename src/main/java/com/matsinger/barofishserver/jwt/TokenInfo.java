package com.matsinger.barofishserver.jwt;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenInfo {

    public TokenInfo (Integer id, TokenAuthType type) {
        this.id = id;
        this.type = type;
    }

    public TokenInfo () {
    }

    private Integer id;

    @Enumerated(EnumType.STRING)
    private TokenAuthType type;
}
