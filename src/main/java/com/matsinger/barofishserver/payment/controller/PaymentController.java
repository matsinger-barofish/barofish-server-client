package com.matsinger.barofishserver.payment.controller;

import com.matsinger.barofishserver.payment.dto.request.PortOnePaymentRequestDto;
import com.matsinger.barofishserver.payment.dto.request.PortOnePriceValidationDto;
import com.matsinger.barofishserver.payment.dto.request.PortOneWebhookReqDto;
import com.matsinger.barofishserver.payment.dto.response.PaymentResponseDto;
import com.matsinger.barofishserver.payment.dto.response.PaymentSuccessResponseDto;
import com.matsinger.barofishserver.payment.exception.PaymentBusinessException;
import com.matsinger.barofishserver.payment.exception.PaymentErrorMessage;
import com.matsinger.barofishserver.payment.service.PaymentCommandService;
import com.matsinger.barofishserver.payment.service.PaymentQueryService;
import com.matsinger.barofishserver.payment.service.PortOneTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentQueryService paymentQueryService;
    private final PaymentCommandService paymentCommandService;

    private final PortOneTokenService portOneTokenService;

    private final String clientIp1 = "52.78.100.19";
    private final String clientIp2 = "52.78.48.223";
    private final String testClientIp = "52.78.5.241";

    @GetMapping("/v1/payment/getKeys")
    public ResponseEntity<Object> getPortOneKeys() {
        try {
            Map<String, String> keys = portOneTokenService.getKeys();
            return new ResponseEntity<>(keys, HttpStatus.OK);
        } catch (Exception e) {
            throw new PaymentBusinessException(e.getMessage(), e);
        }
    }

    // 결제 금액 검증
    @PostMapping("/v1/payment/validatePrice")
    public ResponseEntity<Object> paymentPrepare(@RequestBody PortOnePriceValidationDto request) {
        try {
            return paymentQueryService.validatePrice(request);
        } catch (Exception e) {
            throw new PaymentBusinessException(e.getMessage(), e);
        }
    }

    // 결제
    @PostMapping("/v1/payment/success")
    public ResponseEntity<Object> proceedPayment(@RequestBody PortOnePaymentRequestDto request) {
        try {
            PaymentSuccessResponseDto response = paymentCommandService.proceedPayment(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new PaymentBusinessException(e.getMessage(), e);
        }
    }

    // 결제 정보 조회 (DB)
    @GetMapping("/v1/payment/{id}")
    public ResponseEntity<Object> getPayment(@PathVariable("id") int id) {
        try {
            PaymentResponseDto response = paymentQueryService.getPaymentInfo(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new PaymentBusinessException(e.getMessage(), e);
        }
    }

    // 웹훅
    @PostMapping("/v1/payment")
    public ResponseEntity<Object> getPaymentWebhook(@RequestBody PortOneWebhookReqDto request,
                                                    HttpServletRequest servletRequest) {

        String clientIp = servletRequest.getRemoteAddr();
        if (!clientIp.equals(clientIp1) &&
            !clientIp.equals(clientIp2) &&
            !clientIp.equals(testClientIp)) {
            throw new PaymentBusinessException(PaymentErrorMessage.CLIENT_WEBHOOK_IP_INVALID_EXCEPTION);
        }

        portOneTokenService.getToken();
        return new ResponseEntity<>(null);
    }

    @GetMapping("v1/tokenTest")
    public ResponseEntity<Object> tokenTest() {
        portOneTokenService.getToken();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
