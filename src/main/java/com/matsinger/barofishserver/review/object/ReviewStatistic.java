package com.matsinger.barofishserver.review.object;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewStatistic {
    String key;
    Integer count;
}
