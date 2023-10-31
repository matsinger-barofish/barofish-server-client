package com.matsinger.barofishserver.domain.review.dto.v2;

import com.matsinger.barofishserver.domain.review.domain.ReviewEvaluationType;
import lombok.Getter;

@Getter
public class ReviewEvaluationSummaryDto {

    private ReviewEvaluationType evaluationType;
    private Long evaluationSum;

    @Override
    public String toString() {
        return "ReviewEvaluationSummaryDto{" +
                "evaluationType=" + evaluationType +
                ", evaluationSum=" + evaluationSum +
                '}';
    }
}
