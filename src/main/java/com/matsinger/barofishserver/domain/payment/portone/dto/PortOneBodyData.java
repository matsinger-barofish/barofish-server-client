package com.matsinger.barofishserver.domain.payment.portone.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Data
@NoArgsConstructor
public class PortOneBodyData {
    private String imp_uid;
    private String merchant_uid;
    private String status;
}
