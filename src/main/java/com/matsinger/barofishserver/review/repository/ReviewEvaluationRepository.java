package com.matsinger.barofishserver.review.repository;

import com.matsinger.barofishserver.review.domain.ReviewEvaluation;
import com.matsinger.barofishserver.review.domain.ReviewEvaluationId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewEvaluationRepository extends JpaRepository<ReviewEvaluation, ReviewEvaluationId> {
    List<ReviewEvaluation> findAllByReviewId(Integer reviewId);

    void deleteAllByReviewId(Integer reviewId);
}
