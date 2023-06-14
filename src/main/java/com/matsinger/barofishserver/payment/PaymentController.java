package com.matsinger.barofishserver.payment;

import com.matsinger.barofishserver.jwt.JwtService;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.jwt.TokenInfo;
import com.matsinger.barofishserver.order.OrderService;
import com.matsinger.barofishserver.order.object.OrderState;
import com.matsinger.barofishserver.order.object.Orders;
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

    @GetMapping("/test")
    public ResponseEntity<CustomResponse<Boolean>> paymentTest(@RequestParam(value = "impUid") String impUid) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        try {
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<CustomResponse<Boolean>> cancelOrder(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                               @PathVariable("orderId") String orderId) {
        CustomResponse<Boolean> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.PARTNER), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer storeId = tokenInfo.get().getId();
            Orders order = orderService.selectOrder(orderId);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
