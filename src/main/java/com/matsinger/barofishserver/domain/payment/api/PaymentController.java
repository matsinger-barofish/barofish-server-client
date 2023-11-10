package com.matsinger.barofishserver.domain.payment.api;

import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.domain.order.application.OrderService;
import com.matsinger.barofishserver.domain.order.domain.Orders;
import com.matsinger.barofishserver.domain.payment.application.PaymentService;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final JwtService jwt;

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<CustomResponse<Boolean>> cancelOrder(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                               @PathVariable("orderId") String orderId) {
        CustomResponse<Boolean> res = new CustomResponse<>();

        if (auth.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }
        TokenInfo tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN, TokenAuthType.PARTNER), auth.get());

        Integer storeId = tokenInfo.getId();
        Orders order = orderService.selectOrder(orderId);
        return ResponseEntity.ok(res);
    }
}
