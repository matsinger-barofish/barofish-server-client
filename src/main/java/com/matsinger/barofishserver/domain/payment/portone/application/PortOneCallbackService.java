package com.matsinger.barofishserver.domain.payment.portone.application;

import com.siot.IamportRestClient.IamportClient;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


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

    public IamportClient getIamportClient() {
        return new IamportClient(accessKey, secretKey);
    }
}
