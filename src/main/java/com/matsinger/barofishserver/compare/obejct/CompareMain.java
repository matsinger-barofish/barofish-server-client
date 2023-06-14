package com.matsinger.barofishserver.compare.obejct;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompareMain {
    List<CompareObject.CompareSetDto> popularCompareSets = new ArrayList<>();
    List<CompareObject.RecommendCompareProduct> recommendCompareProducts = new ArrayList<>();
    CompareObject.NewCompareProduct newCompareProduct;
}
