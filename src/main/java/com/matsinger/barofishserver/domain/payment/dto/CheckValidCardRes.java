package com.matsinger.barofishserver.domain.payment.dto;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckValidCardRes {
    String customerUid;
    String cardName;
}
