package com.matsinger.barofishserver.domain.order.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointRuleRes {
    Float pointRate;
    Integer maxReviewPoint;
    Integer ImageReviewPoint;
}
