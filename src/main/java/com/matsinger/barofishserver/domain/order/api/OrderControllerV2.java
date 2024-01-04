package com.matsinger.barofishserver.domain.order.api;

import com.matsinger.barofishserver.domain.order.application.OrderCommandService;
import com.matsinger.barofishserver.domain.order.domain.OrderPaymentWay;
import com.matsinger.barofishserver.domain.order.dto.OrderDto;
import com.matsinger.barofishserver.domain.order.dto.OrderReq;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/order")
public class OrderControllerV2 {

    private final JwtService jwt;
    private final OrderCommandService orderCommandService;
    private final Common utils;

    @PostMapping("")
    public ResponseEntity<CustomResponse<OrderDto>> orderProductV2(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                   @RequestBody OrderReq request) {
        CustomResponse<OrderDto> res = new CustomResponse<>();
        utils.validateString(request.getName(), 20L, "주문자 이름");
        utils.validateString(request.getTel(), 11L, "주문자 연락처");

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        Integer userId = tokenInfo.getId();

        if (request.getPaymentWay().equals(OrderPaymentWay.VIRTUAL_ACCOUNT)) {
            orderCommandService.proceedVirtualAccountOrder(request, userId);
        }
        orderCommandService.proceedOrder(request, tokenInfo.getId());
        return ResponseEntity.ok(res);
    }
}
