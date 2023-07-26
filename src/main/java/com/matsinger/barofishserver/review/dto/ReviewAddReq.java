package com.matsinger.barofishserver.review.dto;

import com.matsinger.barofishserver.review.domain.ReviewEvaluationType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReviewAddReq {
    Integer productId;
    Integer userId;
    Integer orderProductInfoId;
    List<ReviewEvaluationType> evaluations;
    String content;
}
