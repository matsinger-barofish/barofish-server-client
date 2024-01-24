package com.matsinger.barofishserver.domain.payment.portone.application;

import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneAccessKeyRequest;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneAccessKeyResponse;
import com.matsinger.barofishserver.domain.payment.portone.dto.PortOneVbankHolderResponse;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<PortOneVbankHolderResponse> checkVbankAccountVerification(String bankCode, String bankNum) {
        URI uri = UriComponentsBuilder
                .fromUriString(portOneUrl)
                .path("vbanks/holder")
                .queryParam("bank_code", bankCode)
                .queryParam("bank_num", bankNum)
                .encode()
                .build()
                .toUri();

        try {
            ResponseEntity<PortOneVbankHolderResponse> responseEntity = sendPortOneAccountInfo(uri);
            return responseEntity;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                generateAccessToken();
                ResponseEntity<PortOneVbankHolderResponse> responseEntity = sendPortOneAccountInfo(uri);
                return responseEntity;
            }
            return null;
        }
    }

    private ResponseEntity<PortOneVbankHolderResponse> sendPortOneAccountInfo(URI uri) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<PortOneVbankHolderResponse> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                request,
                PortOneVbankHolderResponse.class
        );
        return responseEntity;
    }
}
