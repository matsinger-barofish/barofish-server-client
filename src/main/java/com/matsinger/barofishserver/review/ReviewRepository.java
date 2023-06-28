package com.matsinger.barofishserver.review;

import com.matsinger.barofishserver.review.object.Review;
import com.matsinger.barofishserver.review.object.ReviewStatistic;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer>, JpaSpecificationExecutor<Review> {

    public Page<Review> findAllByUserId(Integer userId, Pageable pageable);

    Integer countAllByUserId(Integer userId);

    Integer countAllByProductId(Integer productId);

    public Page<Review> findAllByStoreId(Integer storeId, Pageable pageable);

    public Page<Review> findAllByProductId(Integer productId, Pageable pageable);

    @Query(value = "SELECT re.evaluation, COUNT( re.evaluation ) AS count\n" +
            "FROM review r\n" +
            "         JOIN review_evaluation re ON r.id = re.review_id\n" +
            "WHERE r.product_id = :productId\n" +
            "GROUP BY re.evaluation;", nativeQuery = true)
    public List<Tuple> selectReviewStatisticsWithProductId(Integer productId);


    @Query(value = "SELECT r.evaluation, COUNT( r.evaluation ) as count\n" +
            "FROM review r\n" +
            "WHERE store_id = :storeId\n" +
            "GROUP BY r.evaluation;", nativeQuery = true)
    public List<Tuple> selectReviewStatisticsWithStoreId(Integer storeId);

    @Query(value = "SELECT COUNT(*) as count FROM review_like WHERE review_id = :reviewId", nativeQuery = true)
    public Integer selectReviewLikeCountWithReviewId(Integer reviewId);

    void deleteAllByUserId(Integer userId);

    Boolean existsByUserIdAndProductIdAndOrderId(Integer userId, Integer productId, String orderId);
}
