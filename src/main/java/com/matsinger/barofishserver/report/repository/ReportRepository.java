package com.matsinger.barofishserver.report.repository;

import com.matsinger.barofishserver.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ReportRepository extends JpaRepository<Report, Integer>, JpaSpecificationExecutor<Report> {
    void deleteAllByReviewId(Integer reviewId);

    Boolean existsByUserIdAndReviewId(Integer userId, Integer reviewId);
}
