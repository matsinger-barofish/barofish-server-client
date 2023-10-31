package com.matsinger.barofishserver.domain.compare.recommend.dto;

import com.matsinger.barofishserver.domain.compare.recommend.domain.RecommendCompareSetType;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;
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
