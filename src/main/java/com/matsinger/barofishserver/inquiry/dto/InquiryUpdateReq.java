package com.matsinger.barofishserver.inquiry.dto;

import com.matsinger.barofishserver.inquiry.domain.InquiryType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InquiryUpdateReq {
    InquiryType type;
    String content;
    Boolean isSecret;
}
