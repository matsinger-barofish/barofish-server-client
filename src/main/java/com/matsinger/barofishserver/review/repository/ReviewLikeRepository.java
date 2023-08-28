package com.matsinger.barofishserver.review.repository;

import com.matsinger.barofishserver.review.domain.ReviewLike;
import com.matsinger.barofishserver.review.domain.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
    Boolean existsByUserIdAndReviewId(Integer userId, Integer reviewId);

    Integer countAllByReviewId(Integer reviewId);

    void deleteAllByReviewId(Integer reviewId);

    void deleteAllByUserIdIn(List<Integer> userIds);
}
