package com.matsinger.barofishserver.domain.review.repository;

import com.matsinger.barofishserver.domain.review.domain.ReviewLike;
import com.matsinger.barofishserver.domain.review.domain.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
    Boolean existsByUserIdAndReviewId(Integer userId, Integer reviewId);

    Integer countAllByReviewId(Integer reviewId);

    void deleteAllByReviewId(Integer reviewId);

    void deleteAllByUserIdIn(List<Integer> userIds);

    Optional<ReviewLike> findByReviewIdAndUserId(Integer id, Integer userId);
}
