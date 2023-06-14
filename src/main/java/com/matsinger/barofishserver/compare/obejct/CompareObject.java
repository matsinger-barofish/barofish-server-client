package com.matsinger.barofishserver.compare.obejct;

import com.matsinger.barofishserver.product.object.ProductListDto;
import lombok.*;

import java.util.List;

public class CompareObject {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompareSetDto {
        Integer compareSetId;
        List<ProductListDto> products;
    }
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendCompareProduct {
        ProductListDto mainProduct;
        List<ProductListDto> recommendProducts;
    }
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewCompareProduct {
        List<ProductListDto> products;
    }

}
