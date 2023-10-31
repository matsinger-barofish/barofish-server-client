package com.matsinger.barofishserver.domain.compare.dto;

import com.matsinger.barofishserver.domain.compare.recommend.domain.RecommendCompareSetType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class AddRecommendCompareSet {
    RecommendCompareSetType type;
    List<Integer> productIds;
}
