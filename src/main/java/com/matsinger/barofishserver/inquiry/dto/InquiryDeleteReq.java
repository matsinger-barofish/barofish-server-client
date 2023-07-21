package com.matsinger.barofishserver.inquiry.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class InquiryDeleteReq {
    List<Integer> inquiryIds;
}
