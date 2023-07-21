package com.matsinger.barofishserver.dashboard.dto;

import com.matsinger.barofishserver.inquiry.dto.InquiryDto;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashBoard {
    Integer dailyJoinCount;
    List<InquiryDto> inquiries;
    Integer dailyOrderCount;
    Integer dailyOrderAmount;
    List<OrderProductInfo> orderSituation;
    Integer monthlyJoinCount;
    Integer monthlyOrderCount;
    Integer monthlyOrderAmount;
    List<ProductRankDto> dailyMostSoldProduct;
    List<ProductRankDto> monthlyMostSoldProduct;
}
