package com.matsinger.barofishserver.domain.order.api;

import com.matsinger.barofishserver.domain.order.application.OrderCommandService;
import com.matsinger.barofishserver.domain.order.dto.OrderDto;
import com.matsinger.barofishserver.domain.order.dto.OrderReq;
import com.matsinger.barofishserver.domain.order.dto.OrderResponse;
import com.matsinger.barofishserver.domain.order.dto.RequestCancelReq;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

        OrderResponse orderResponse = orderCommandService.proceedOrder(request, tokenInfo.getId());

        res.setIsSuccess(true);
        res.setData(Optional.ofNullable(
                OrderDto.builder()
                        .id(orderResponse.getOrderId())
                        .build())
        );
        if (orderResponse.isCanDeliver()) {
            res.setCode("200");
            return ResponseEntity.ok(res);
        }

        if (!orderResponse.isCanDeliver()) {
            res.setCode("400");
            res.setErrorMsg("배송지에 배송 불가능한 상품이 포함돼 있습니다.");
            return ResponseEntity
                    .badRequest()
                    .body(res);
        }
        return ResponseEntity.ok(res);
    }

    // 결제 취소
    @PostMapping("/cancel/{orderProductInfoId}")
    public ResponseEntity<CustomResponse<Boolean>> cancelOrderByUserV2(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @PathVariable("orderProductInfoId") Integer orderProductInfoId,
                                                                     @RequestPart(value = "data") RequestCancelReq data) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.USER), auth);
        orderCommandService.cancelOrderByUser(tokenInfo.getId(), orderProductInfoId, data);

        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }

    @PostMapping("/cancel/partner")
    public ResponseEntity<CustomResponse<Boolean>> cancelOrdersByPartnerV2(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                         @RequestPart(value = "orderProductInfoIds") List<Integer> orderProductInfoIds) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER, TokenAuthType.ADMIN), auth);

        res.setData(Optional.of(true));
        return ResponseEntity.ok(res);
    }
}
