package com.matsinger.barofishserver.review;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewStatistic {
    String key;
    Integer count;
}
