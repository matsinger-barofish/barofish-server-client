package com.matsinger.barofishserver.domain.review.dto;

import com.matsinger.barofishserver.domain.review.domain.ReviewEvaluationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateReviewReq {
    String content;
    List<ReviewEvaluationType> evaluations;
}
