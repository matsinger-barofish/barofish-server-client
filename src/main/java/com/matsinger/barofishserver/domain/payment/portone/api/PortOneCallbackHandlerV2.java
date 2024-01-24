package com.matsinger.barofishserver.domain.payment.portone.api;

import com.matsinger.barofishserver.domain.coupon.application.CouponCommandService;
import com.matsinger.barofishserver.domain.order.application.OrderCommandService;
import com.matsinger.barofishserver.domain.order.application.OrderQueryService;
import com.matsinger.barofishserver.domain.order.orderprductinfo.repository.OrderProductInfoRepository;
import com.matsinger.barofishserver.domain.order.repository.OrderRepository;
import com.matsinger.barofishserver.domain.payment.application.PaymentService;
import com.matsinger.barofishserver.domain.payment.portone.application.PortOneCommandService;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneBodyData;
import com.matsinger.barofishserver.domain.payment.repository.PaymentRepository;
import com.matsinger.barofishserver.domain.userinfo.application.UserInfoQueryService;
import com.matsinger.barofishserver.domain.userinfo.repository.UserInfoRepository;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback/iamport_pay_result")
public class PortOneCallbackHandlerV2 {

    private final PortOneCommandService portOneCommandService;
    private final OrderQueryService orderQueryService;
    private final PaymentService paymentService;
    private final UserInfoQueryService userInfoQueryService;
    private final CouponCommandService couponCommandService;
    private final OrderCommandService orderCommandService;
    private final OrderRepository orderRepository;
    private final OrderProductInfoRepository orderProductInfoRepository;
    private final PaymentRepository paymentRepository;
    private final UserInfoRepository userInfoRepository;

    @PostMapping("")
    public ResponseEntity<Object> portOneCallback(@RequestHeader(value = "x-real-ip", required = false) String XRealIp,
                                                  @RequestBody(required = false) PortOneBodyData request) throws IamportResponseException, IOException {
        System.out.println("Portone callback received");
        if (!XRealIp.equals("52.78.100.19") && !XRealIp.equals("52.78.48.223")) {
            return ResponseEntity.status(403).body(null);
        }

        if (request.getStatus().equals("ready")) {
            portOneCommandService.processWhenStatusReady(request);
        }

        if (request.getStatus().equals("paid")) {
            portOneCommandService.checkCanDeliverAndProcessOrder(request);
        }

        if (request.getStatus().equals("canceled")) {
            portOneCommandService.processWhenStatusCanceled(request);
        }
        return ResponseEntity.ok(null);
    }
}
