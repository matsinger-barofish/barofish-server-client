package com.matsinger.barofishserver.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnsJoinLoginResponseDto {

    private int userId;
    private String loginId;
}
