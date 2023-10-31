package com.matsinger.barofishserver.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateFcmReq {

    private String fcmToken;
    private Boolean set;
}
