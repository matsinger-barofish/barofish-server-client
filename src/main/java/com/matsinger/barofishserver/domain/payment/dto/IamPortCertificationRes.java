package com.matsinger.barofishserver.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IamPortCertificationRes {
    String impUid;
    String name;
    String phone;
    Boolean certified;
    String certifiedAt;
}
