package com.matsinger.barofishserver.domain.review.repository;

import com.matsinger.barofishserver.domain.review.domain.ReviewEvaluation;
import com.matsinger.barofishserver.domain.review.domain.ReviewEvaluationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewEvaluationRepository extends JpaRepository<ReviewEvaluation, ReviewEvaluationId> {
    List<ReviewEvaluation> findAllByReviewId(Integer reviewId);

    void deleteAllByReviewId(Integer reviewId);

    void deleteAllByReview_UserIdIn(List<Integer> userIds);
}
