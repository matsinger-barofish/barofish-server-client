package com.matsinger.barofishserver.domain.user.dto;

import com.matsinger.barofishserver.jwt.Jwt;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class JoinResponse {

    private boolean isNew;
    private Jwt jwt;
}
