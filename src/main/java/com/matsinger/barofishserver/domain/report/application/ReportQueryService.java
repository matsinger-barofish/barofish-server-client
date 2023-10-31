package com.matsinger.barofishserver.domain.report.application;

import com.matsinger.barofishserver.domain.report.domain.Report;
import com.matsinger.barofishserver.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReportQueryService {
    private final ReportRepository reportRepository;

    public Boolean checkHasReported(Integer userId, Integer reviewId) {
        return reportRepository.existsByUserIdAndReviewId(userId, reviewId);
    }

    public List<Report> selectReportListWithIds(List<Integer> ids) {
        return reportRepository.findAllById(ids);
    }

    public Page<Report> selectReportList(PageRequest pageRequest, Specification<Report> spec) {
        return reportRepository.findAll(spec, pageRequest);
    }

    public Report selectReport(Integer id) {
        return reportRepository.findById(id).orElseThrow(() -> {
            throw new Error("신고 내역을 찾을 수 없습니다.");
        });
    }
}
