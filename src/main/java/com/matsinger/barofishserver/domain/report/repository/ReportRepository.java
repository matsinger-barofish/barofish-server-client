package com.matsinger.barofishserver.domain.report.repository;

import com.matsinger.barofishserver.domain.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;


public interface ReportRepository extends JpaRepository<Report, Integer>, JpaSpecificationExecutor<Report> {
    void deleteAllByReviewId(Integer reviewId);

    Boolean existsByUserIdAndReviewId(Integer userId, Integer reviewId);

    void deleteAllByUserIdIn(List<Integer> userIds);
}
