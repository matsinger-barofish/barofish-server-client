package com.matsinger.barofishserver.domain.report.api;

import com.matsinger.barofishserver.domain.admin.log.application.AdminLogCommandService;
import com.matsinger.barofishserver.domain.admin.log.application.AdminLogQueryService;
import com.matsinger.barofishserver.domain.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.domain.admin.log.domain.AdminLogType;
import com.matsinger.barofishserver.domain.report.dto.ReportDto;
import com.matsinger.barofishserver.domain.review.application.ReviewQueryService;
import com.matsinger.barofishserver.domain.review.domain.Review;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.report.application.ReportCommandService;
import com.matsinger.barofishserver.domain.report.application.ReportQueryService;
import com.matsinger.barofishserver.domain.report.domain.ReportOrderBy;
import com.matsinger.barofishserver.domain.report.domain.Report;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/report")
public class ReportController {
    private final JwtService jwt;
    private final ReportQueryService reportQueryService;
    private final ReportCommandService reportCommandService;
    private final ReviewQueryService reviewQueryService;
    private final AdminLogCommandService adminLogCommandService;
    private final AdminLogQueryService adminLogQueryService;
    private final Common utils;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<Page<ReportDto>>> selectReportList(@RequestHeader(value = "Authorization", required = false) Optional<String> auth,
                                                                            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                            @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                            @RequestParam(value = "orderby", defaultValue = "createdAt") ReportOrderBy orderBy,
                                                                            @RequestParam(value = "orderType", defaultValue = "DESC") Sort.Direction sort,
                                                                            @RequestParam(value = "reportFrom", required = false) String reportFrom,
                                                                            @RequestParam(value = "reportTo", required = false) String reportTo,
                                                                            @RequestParam(value = "createdAtS", required = false) Timestamp createdAtS,
                                                                            @RequestParam(value = "createdAtE", required = false) Timestamp createdAtE) {
        CustomResponse<Page<ReportDto>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Specification<Report> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (reportFrom != null)
                    predicates.add(builder.like(root.get("user").get("userInfo").get("name"), "%" + reportFrom + "%"));
                if (reportTo != null)
                    predicates.add(builder.like(root.get("review").get("user").get("userInfo").get("name"),
                            "%" + reportTo + "%"));
                if (createdAtS != null) predicates.add(builder.greaterThan(root.get("createdAt"), createdAtS));
                if (createdAtE != null) predicates.add(builder.lessThan(root.get("createdAt"), createdAtE));
                if (tokenInfo.get().getType().equals(TokenAuthType.PARTNER))
                    predicates.add(builder.equal(root.get("product").get("storeId"), tokenInfo.get().getId()));
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(sort, orderBy.label));
            Page<ReportDto>
                    reportDtos =
                    reportQueryService.selectReportList(pageRequest, spec).map(reportCommandService::convert2Dto);
            res.setData(Optional.of(reportDtos));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<ReportDto>> selectReport(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @PathVariable("id") Integer id) {
        CustomResponse<ReportDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Report report = reportQueryService.selectReport(id);
            ReportDto reportDto = reportCommandService.convert2Dto(report);
            res.setData(Optional.ofNullable(reportDto));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class AddReportReq {
        Integer reviewId;
        String content;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<ReportDto>> addReport(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                               @RequestPart(value = "data") AddReportReq data) {
        CustomResponse<ReportDto> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer userId = tokenInfo.get().getId();
            if (data.reviewId == null) return res.throwError("리뷰 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            if (reportQueryService.checkHasReported(userId, data.reviewId))
                return res.throwError("이미 신고한 리뷰입니다.", "INPUT_CHECK_REQUIRED");
            Review review = reviewQueryService.selectReview(data.reviewId);
            String content = utils.validateString(data.content, 300L, "내용");
            Report
                    report =
                    Report.builder().userId(userId).reviewId(review.getId()).content(content).createdAt(utils.now()).build();
            reportCommandService.addReport(report);
            res.setData(Optional.ofNullable(reportCommandService.convert2Dto(report)));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class confirmReportReq {
        List<Integer> reportIds;
    }

    @PostMapping("/confirm")
    public ResponseEntity<CustomResponse<Boolean>> confirmReports(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @RequestPart(value = "data") confirmReportReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = tokenInfo.get().getId();
            List<Report> reports = reportQueryService.selectReportListWithIds(data.getReportIds());
            for (Report report : reports) {
                report.setConfirmAt(utils.now());
                AdminLog
                        adminLog =
                        AdminLog.builder().id(adminLogQueryService.getAdminLogId()).adminId(adminId).type(AdminLogType.REPORT).targetId(
                                String.valueOf(report.getId())).content("신고 확인 처리되었습니다.").createdAt(utils.now()).build();
                adminLogCommandService.saveAdminLog(adminLog);
            }
            List<Report> result = reportCommandService.updateManyReport(reports);

            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class deleteReportReq {
        List<Integer> reportIds;
    }

    @DeleteMapping("")
    public ResponseEntity<CustomResponse<Boolean>> deleteReports(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                 @RequestPart(value = "data") deleteReportReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            reportCommandService.deleteReportList(data.getReportIds());
            res.setData(Optional.of(true));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
