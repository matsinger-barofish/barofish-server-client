package com.matsinger.barofishserver.report;

import com.matsinger.barofishserver.review.object.ReviewDto;
import com.matsinger.barofishserver.review.ReviewService;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.user.object.UserInfoDto;
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
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final ReviewService reviewService;

    public ReportDto convert2Dto(Report report) {
        ReportDto reportDto = report.convert2Dto();
        UserInfoDto user = userService.selectUserInfo(report.getUserId()).convert2Dto();
        user.setUser(userService.selectUser(report.getUserId()).convert2Dto());
        ReviewDto review = reviewService.convert2Dto(reviewService.selectReview(report.getReviewId()));
        reportDto.setUser(user);
        reportDto.setReview(review);
        return reportDto;
    }

    public List<Report> selectReportListWithIds(List<Integer> ids) {
        return reportRepository.findAllById(ids);
    }

    public List<Report> updateManyReport(List<Report> reports) {
        return reportRepository.saveAll(reports);
    }

    public Report addReport(Report report) {
        return reportRepository.save(report);
    }

    public Report updateReport(Report report) {
        return reportRepository.save(report);
    }

    public Page<Report> selectReportList(PageRequest pageRequest, Specification<Report> spec) {
        return reportRepository.findAll(spec, pageRequest);
    }

    public Report selectReport(Integer id) {
        return reportRepository.findById(id).orElseThrow(() -> {
            throw new Error("신고 내역을 찾을 수 없습니다.");
        });
    }

    public void deleteReport(Integer id) {
        reportRepository.deleteById(id);
    }

    public void deleteReportList(List<Integer> ids) {
        reportRepository.deleteAllById(ids);
    }

}
