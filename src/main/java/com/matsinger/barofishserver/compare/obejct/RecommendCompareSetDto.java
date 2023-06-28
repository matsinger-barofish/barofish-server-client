package com.matsinger.barofishserver.compare.obejct;

import com.matsinger.barofishserver.product.object.ProductListDto;
import lombok.*;

import java.util.List;
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendCompareSetDto {
    Integer id;
    RecommendCompareSetType type;
    List<ProductListDto> products;
}
