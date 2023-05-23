package com.matsinger.barofishserver.payment.controller;

import com.matsinger.barofishserver.payment.exception.PaymentBusinessException;
import com.matsinger.barofishserver.payment.service.PaymentCommandService;
import com.matsinger.barofishserver.payment.service.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

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

//    @PostMapping("/")
}
