package com.matsinger.barofishserver.domain.review.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewEvaluationId implements Serializable {
    private Integer reviewId;
    private ReviewEvaluationType evaluation;
}