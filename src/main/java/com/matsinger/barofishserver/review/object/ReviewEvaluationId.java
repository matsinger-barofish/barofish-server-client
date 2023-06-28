package com.matsinger.barofishserver.review.object;

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