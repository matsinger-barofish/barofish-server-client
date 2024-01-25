package com.matsinger.barofishserver.domain.payment.portone.application;

import com.matsinger.barofishserver.domain.order.domain.BankCode;
import com.matsinger.barofishserver.domain.order.repository.BankCodeRepository;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneAccessKeyRequest;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneAccessKeyResponse;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneVbankHolderResponse;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class PortOneQueryService {

    private final BankCodeRepository bankCodeRepository;

    @Value("${iamport.credentials.apiKey}")
    private String accessKey;

    @Value("${iamport.credentials.secretKey}")
    private String secretKey;

    private String portOneUrl = "https://api.iamport.kr/";
    
    private String accessToken;

    public void generateAccessToken() {
//        WebClient webClient = WebClient.builder().build();
//
//        PortOneAccessKeyResponse response = webClient.post()
//                .uri(url)
//                .bodyValue(PortOneAccessKeyRequest.class)
//                .retrieve()
//                .bodyToMono(PortOneAccessKeyResponse.class)
//                .block();
//
//        AccessKeyResponseBody responseBody = response.getResponse();
//        this.accessToken = responseBody.getAccessToken();

        URI uri = UriComponentsBuilder
                .fromUriString(portOneUrl)
                .path("users/getToken")
                .encode()
                .build()
                .toUri();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        PortOneAccessKeyRequest requestDto = new PortOneAccessKeyRequest(accessKey, secretKey);
        HttpEntity<PortOneAccessKeyRequest> request = new HttpEntity<>(requestDto, headers);

        ResponseEntity<PortOneAccessKeyResponse> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.POST,
                request,
                PortOneAccessKeyResponse.class
        );

        this.accessToken = responseEntity.getBody().getResponse().getAccess_token();
    }
    public String checkVbankAccountVerification(Integer bankCodeId, String bankNum, String holderName) {
        BankCode bankCode = bankCodeRepository.findById(bankCodeId)
                .orElseThrow(() -> new BusinessException("은행 코드를 찾을 수 없습니다."));
        URI uri = UriComponentsBuilder
                .fromUriString(portOneUrl)
                .path("vbanks/holder")
                .queryParam("bank_code", bankCode.getCode())
                .queryParam("bank_num", bankNum)
                .encode()
                .build()
                .toUri();

        return sendPortOneAccountInfo(uri, holderName);
    }

    private String sendPortOneAccountInfo(URI uri, String holderName) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity request = new HttpEntity(headers);

        String errorMessage = null;
        int trialCount = 0;
        try {
            ResponseEntity<PortOneVbankHolderResponse> responseEntity = checkBankAccountValid(uri, restTemplate, request);
            String bankHolderResponse = responseEntity.getBody().getResponse().getBank_holder();
            if (!bankHolderResponse.equals(holderName)) {
                errorMessage = "계좌 소유주 명이 일치하지 않습니다.";
            }
        } catch (HttpClientErrorException e) {
            trialCount++;
            if (trialCount < 2) {
                if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                    generateAccessToken();
                    ResponseEntity<PortOneVbankHolderResponse> responseEntity = checkBankAccountValid(uri, restTemplate, request);
                    String bankHolderResponse = responseEntity.getBody().getResponse().getBank_holder();
                    if (!bankHolderResponse.equals(holderName)) {
                        errorMessage = "계좌 소유주 명이 일치하지 않습니다.";
                    }
                }
            }

            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                errorMessage = "은행, 또는 계좌번호를 입력해주세요.";
            }
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                errorMessage = "계좌 번호가 일치하지 않습니다.";
            }
        }
        return errorMessage;
    }

    @NotNull
    private static ResponseEntity<PortOneVbankHolderResponse> checkBankAccountValid(URI uri, RestTemplate restTemplate, HttpEntity request) {
        ResponseEntity<PortOneVbankHolderResponse> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                PortOneVbankHolderResponse.class
        );
        return responseEntity;
    }
}
