package com.matsinger.barofishserver.compare.recommend.dto;

import com.matsinger.barofishserver.compare.recommend.domain.RecommendCompareSetType;
import com.matsinger.barofishserver.product.dto.ProductListDto;
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
