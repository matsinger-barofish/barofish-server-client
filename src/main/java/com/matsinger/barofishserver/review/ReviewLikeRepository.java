package com.matsinger.barofishserver.review;

import com.matsinger.barofishserver.review.object.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
    Boolean existsByUserIdAndReviewId(Integer userId, Integer reviewId);

    Integer countAllByReviewId(Integer reviewId);

    void deleteAllByReviewId(Integer reviewId);
}
