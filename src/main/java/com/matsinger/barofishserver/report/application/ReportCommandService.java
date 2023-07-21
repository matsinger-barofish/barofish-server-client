package com.matsinger.barofishserver.report.application;

import com.matsinger.barofishserver.report.domain.Report;
import com.matsinger.barofishserver.report.dto.ReportDto;
import com.matsinger.barofishserver.report.repository.ReportRepository;
import com.matsinger.barofishserver.review.application.ReviewService;
import com.matsinger.barofishserver.review.dto.ReviewDto;
import com.matsinger.barofishserver.user.application.UserCommandService;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ReportCommandService {
    private final ReportRepository reportRepository;
    private final UserCommandService userService;
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

    public List<Report> updateManyReport(List<Report> reports) {
        return reportRepository.saveAll(reports);
    }

    public void addReport(Report report) {
        reportRepository.save(report);
    }

    public Report updateReport(Report report) {
        return reportRepository.save(report);
    }

    public void deleteReport(Integer id) {
        reportRepository.deleteById(id);
    }

    public void deleteReportList(List<Integer> ids) {
        reportRepository.deleteAllById(ids);
    }
}
