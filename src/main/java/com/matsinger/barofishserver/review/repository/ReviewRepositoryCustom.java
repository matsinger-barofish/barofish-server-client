package com.matsinger.barofishserver.review.repository;

import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.review.domain.ReviewOrderByType;
import com.matsinger.barofishserver.review.dto.v2.ReviewDtoV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepositoryCustom {

    Page<ReviewDtoV2> getReviewsWithProduct(Integer productId, ReviewOrderByType orderType, Pageable pageable);
}
