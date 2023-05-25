package com.matsinger.barofishserver.payment.controller;

import com.matsinger.barofishserver.payment.dto.request.PortOnePaymentRequestDto;
import com.matsinger.barofishserver.payment.dto.request.PortOnePriceValidationDto;
import com.matsinger.barofishserver.payment.dto.response.PaymentResponseDto;
import com.matsinger.barofishserver.payment.dto.response.PaymentSuccessResponseDto;
import com.matsinger.barofishserver.payment.exception.PaymentBusinessException;
import com.matsinger.barofishserver.payment.service.PaymentCommandService;
import com.matsinger.barofishserver.payment.service.PaymentQueryService;
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

    @GetMapping("/v1/payment/getKeys")
    public ResponseEntity<Object> getPortOneKeys() {
        try {
            Map<String, String> keys = paymentQueryService.getKeys();
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
    // TODO: 결제 하기 전에 option 수량 검증하는 로직 추가
    @PostMapping("/v1/payment/success")
    public ResponseEntity<Object> proceedPayment(@RequestBody PortOnePaymentRequestDto request) {
        try {
            PaymentSuccessResponseDto response = paymentCommandService.proceedPayment(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new PaymentBusinessException(e.getMessage(), e);
        }
    }

    // 결제 정보 조회
    @GetMapping("/vi/payment/{id}")
    public ResponseEntity<Object> getPayment(@PathVariable("id") int id) {
        try {
            PaymentResponseDto response = paymentQueryService.getPaymentInfo(id);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            throw new PaymentBusinessException(e.getMessage(), e);
        }
    }
}
