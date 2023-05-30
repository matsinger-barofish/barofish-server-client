package com.matsinger.barofishserver.payment.service;

import com.matsinger.barofishserver.payment.dto.PortOneTokenReqDto;
import com.matsinger.barofishserver.payment.dto.PortOneTokenInfo;
import com.matsinger.barofishserver.payment.dto.PortOneTokenResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PortOneTokenService {

    private final String identificationCode = "imp38983932";
    private final String impKey = "8069640719378424";
    private final String impSecret = "9163909ecb98ff8fc1e0ced24d3ed59583f740d446bd8be477ed1e3110032b54f610f664c2a1e81e";

    private final String baseUrl = "https://api.iamport.kr/";

    private PortOneTokenInfo tokenInfo;


    public PortOneTokenInfo getToken() {
        long expiryTime = this.tokenInfo.getExpired_at() / 1000;
        long currentTime = System.currentTimeMillis() / 1000;

        if (expiryTime < currentTime) {
            this.tokenInfo = requestToken();
        }
        return this.tokenInfo;
    }

    private PortOneTokenInfo requestToken() {
        String requestUrl = baseUrl + "users/getToken";

        Map<String, String> keys = getKeys();
        PortOneTokenReqDto tokenDto = PortOneTokenReqDto.builder()
                .imp_key(keys.get("impKey"))
                .imp_secret(keys.get("impSecret")).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<PortOneTokenReqDto> httpEntity = new HttpEntity<>(tokenDto, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PortOneTokenResDto> response = restTemplate.postForEntity(requestUrl, httpEntity, PortOneTokenResDto.class);

        return response.getBody().getResponse();
    }

    public Map<String, String> getKeys() {
        Map<String, String> keys = new HashMap<>();
        keys.put("identificationCode", identificationCode);
        keys.put("impKey", impKey);
        keys.put("impSecret", impSecret);
        return keys;
    }
}
