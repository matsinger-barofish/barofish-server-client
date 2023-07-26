package com.matsinger.barofishserver.payment.dto;

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
