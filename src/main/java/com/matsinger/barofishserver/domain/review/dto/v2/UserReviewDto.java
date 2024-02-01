package com.matsinger.barofishserver.domain.review.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder @AllArgsConstructor @NoArgsConstructor
public class UserReviewDto {

    private Integer userId;
    private Long reviewCount;

    private Page<ReviewDtoV2> pagedReviews;
}
