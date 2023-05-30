package com.matsinger.barofishserver.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PortOneQueryService {

    private final String baseUrl = "https://api.iamport.kr/";

    private final PortOneTokenService portOneTokenService;

    public void getPortOnePaymentInfo(String imp_uid) {
        String requestUrl = baseUrl + "payments/" +imp_uid;
    }
}
