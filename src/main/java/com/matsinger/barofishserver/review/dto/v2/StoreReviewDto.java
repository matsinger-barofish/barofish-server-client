package com.matsinger.barofishserver.review.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class StoreReviewDto {

    private Integer storeId;
    private Long reviewCount;

    private List<ReviewEvaluationSummaryDto> evaluationSummaryDtos;

    private Page<ReviewDtoV2> pagedReviews;
}
