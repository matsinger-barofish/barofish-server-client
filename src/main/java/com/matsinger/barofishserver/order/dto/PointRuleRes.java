package com.matsinger.barofishserver.order.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PointRuleRes {
    Integer pointRate;
    Integer maxReviewPoint;
}
