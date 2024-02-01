package com.matsinger.barofishserver.domain.inquiry.dto;

import com.matsinger.barofishserver.domain.inquiry.domain.InquiryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryAddReq {
    Integer productId;
    InquiryType type;
    String content;
    Boolean isSecret;
}
