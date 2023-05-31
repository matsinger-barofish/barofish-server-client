package com.matsinger.barofishserver.payment.service;

import com.matsinger.barofishserver.payment.Payment;
import com.matsinger.barofishserver.payment.dto.request.PortOnePriceValidationDto;
import com.matsinger.barofishserver.payment.dto.response.PaymentResponseDto;
import com.matsinger.barofishserver.payment.exception.PaymentErrorMessage;
import com.matsinger.barofishserver.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentQueryService {

    private final String baseUrl = "https://api.iamport.kr/payments/";
    private final PaymentRepository paymentRepository;

    public ResponseEntity<Object> validatePrice(PortOnePriceValidationDto request) {

        String requestUrl = baseUrl + "prepare";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PortOnePriceValidationDto> httpEntity = new HttpEntity<>(request, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object> response = restTemplate.postForEntity(requestUrl, httpEntity, Object.class);
        return response;
    }

    public PaymentResponseDto getPaymentInfo(int id) {
        Payment findPayment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException(PaymentErrorMessage.PAYMENT_NOT_FOUND_EXCEPTION);
                });
        return findPayment.toDto();
    }
}
