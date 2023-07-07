package com.matsinger.barofishserver.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ReportRepository extends JpaRepository<Report, Integer>, JpaSpecificationExecutor<Report> {
    void deleteAllByReviewId(Integer reviewId);

    Boolean existsByUserIdAndReviewId(Integer userId, Integer reviewId);
}
