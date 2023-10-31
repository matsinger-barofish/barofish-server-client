package com.matsinger.barofishserver.domain.compare.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompareMain {
    List<CompareSetDto> popularCompareSets = new ArrayList<>();
    List<RecommendCompareProduct> recommendCompareProducts = new ArrayList<>();
    NewCompareProduct newCompareProduct;
}
