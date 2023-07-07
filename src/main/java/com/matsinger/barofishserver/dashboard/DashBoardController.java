package com.matsinger.barofishserver.dashboard;

import com.matsinger.barofishserver.inquiry.InquiryDto;
import com.matsinger.barofishserver.inquiry.InquiryService;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.order.object.OrderProductInfo;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashBoardController {
    private final JwtService jwt;
    private final DashBoardService dashBoardService;
    private final InquiryService inquiryService;


    @GetMapping("")
    public ResponseEntity<CustomResponse<DashBoard>> selectDashBoard(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<DashBoard> res = new CustomResponse<>();
        Optional<TokenInfo>
                tokenInfo =
                jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer storeId = tokenInfo.get().getType().equals(TokenAuthType.PARTNER) ? tokenInfo.get().getId() : null;
            Integer dailyJoinCount = storeId == null ? dashBoardService.selectJoinCount(DashBoardType.DAILY) : null;
            List<InquiryDto>
                    inquiries =
                    storeId == null ? dashBoardService.selectInquiryList(null) : dashBoardService.selectInquiryList(
                            storeId);
            DashBoardService.DashBoardOrderAggregation
                    dailyOrderAggregation =
                    dashBoardService.getOrderCount(DashBoardType.DAILY, storeId);
            Integer dailyOrderCount = dailyOrderAggregation.getCount();
            Integer dailyOrderAmount = dailyOrderAggregation.getAmount();
            List<OrderProductInfo> orderSituation = null;
            Integer monthlyJoinCount = storeId == null ? dashBoardService.selectJoinCount(DashBoardType.MONTHLY) : null;
            DashBoardService.DashBoardOrderAggregation
                    monthlyOrderAggregation =
                    dashBoardService.getOrderCount(DashBoardType.MONTHLY, storeId);
            Integer monthlyOrderCount = monthlyOrderAggregation.getCount();
            Integer monthlyOrderAmount = monthlyOrderAggregation.getAmount();
            List<ProductRankDto> dailyMostSoldProduct = dashBoardService.getProductRankList(DashBoardType.DAILY, null);
            List<ProductRankDto>
                    monthlyMostSoldProduct =
                    dashBoardService.getProductRankList(DashBoardType.MONTHLY, storeId);
            res.setData(Optional.ofNullable(DashBoard.builder().dailyJoinCount(dailyJoinCount).inquiries(inquiries).dailyOrderCount(
                    dailyOrderCount).dailyOrderAmount(dailyOrderAmount).monthlyOrderCount(monthlyOrderCount).monthlyJoinCount(
                    monthlyJoinCount).monthlyOrderAmount(monthlyOrderAmount).dailyMostSoldProduct(dailyMostSoldProduct).monthlyMostSoldProduct(
                    monthlyMostSoldProduct).build()));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
