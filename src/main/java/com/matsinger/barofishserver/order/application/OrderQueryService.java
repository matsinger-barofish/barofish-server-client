package com.matsinger.barofishserver.order.application;

import com.matsinger.barofishserver.category.application.CategoryQueryService;
import com.matsinger.barofishserver.coupon.domain.Coupon;
import com.matsinger.barofishserver.coupon.repository.CouponRepository;
import com.matsinger.barofishserver.order.domain.Orders;
import com.matsinger.barofishserver.order.dto.OrderDto;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.order.orderprductinfo.dto.OrderProductInfoDto;
import com.matsinger.barofishserver.userauth.repository.UserAuthRepository;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderQueryService {


    private final UserAuthRepository userAuthRepository;
    private UserInfoRepository userInfoRepository;
    private CouponRepository couponRepository;

    public OrderDto convertToDto(Orders order) {

        // userInfoDto, couponName, needTaxation, productInfos, deliverPlace 설정해야함
        OrderDto orderDto = order.toDto();

        UserInfo findUserInfo = userInfoRepository.findByUserId(order.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
        findUserInfo.convert2Dto();

        Coupon findCoupon = couponRepository.findById(order.getCouponId())
                .orElseThrow(() -> new IllegalArgumentException("쿠폰 정보를 찾을 수 없습니다."));

        orderDto.setUser(findUserInfo.convert2Dto());
        orderDto.setCouponName(findCoupon.getTitle());


        for (OrderProductInfo productInfo : order.getProductInfos()) {
            // optionItem, optionItemId, deliverFeeType,
            // deliverCompany, product
            OrderProductInfoDto productInfoDto = productInfo.toDto();
            productInfoDto.getOptionItem();
            productInfoDto.getOptionItemId();
            productInfoDto.getDeliverFeeType()
        }

    }
}
