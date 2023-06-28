package com.matsinger.barofishserver.payment;

import com.matsinger.barofishserver.utils.Common;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.response.AccessToken;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
@Service
public class PortOneCallbackService {

    @Value("${iamport.credentials.apiKey}")
    private String accessKey;

    @Value("${iamport.credentials.secretKey}")
    private String secretKey;
    @Value("${iamport.credentials.channel-name}")
    public String channelName;
    @Value("${iamport.webhook.url}")
    public String callbackUrl;

    public final IamportClient iamportClient;

    public PortOneCallbackService() {
        this.iamportClient = new IamportClient(accessKey, secretKey);
    }

    @Getter
    @ToString
    @Data
    @NoArgsConstructor
    public static class PortOneBodyData {
        private String imp_uid;
        private String merchant_uid;
        private String status;
    }

    public IamportClient getIamportClient() {
        return new IamportClient(accessKey, secretKey);
    }
}
