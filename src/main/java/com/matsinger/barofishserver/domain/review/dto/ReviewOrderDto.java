package com.matsinger.barofishserver.domain.review.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewOrderDto {
    String orderId;
    String optionName;
    Integer amount;
}
