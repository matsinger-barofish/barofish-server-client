package com.matsinger.barofishserver.review;

import com.matsinger.barofishserver.review.object.ReviewEvaluation;
import com.matsinger.barofishserver.review.object.ReviewEvaluationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewEvaluationRepository extends JpaRepository<ReviewEvaluation, ReviewEvaluationId> {
    List<ReviewEvaluation> findAllByReviewId(Integer reviewId);

    void deleteAllByReviewId(Integer reviewId);
}
