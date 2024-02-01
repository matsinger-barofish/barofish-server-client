package com.matsinger.barofishserver.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ReviewTotalStatistic {
    Integer taste;
    Integer fresh;
    Integer price;
    Integer packaging;
    Integer size;
}
