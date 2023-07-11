package com.matsinger.barofishserver.utils.sms;

import lombok.*;
import okhttp3.Response;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class SmsService {
    private String BASE_URL = "https://api-sms.cloud.toast.com";
    @Value("${nhn.toast.apiKey}")
    private String apiKey;
    @Value("${nhn.toast.secretKey}")
    private String secretKey;
    @Value("${nhn.toast.sendTel}")
    private String sendTel;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendSmsRecipient {
        String recipientNo;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendSmsReq {
        String body;
        String sendNo;
        String title;
        List<SendSmsRecipient> recipientList;
    }

    public void sendSms(String receiveTel, String content, String title) {
        RestTemplate restTemplate = new RestTemplate();
        boolean isSms = content.getBytes().length <= 90;
        String url = BASE_URL + "/sms/v3.0/appKeys/" + apiKey + "/sender/" + (isSms ? "sms" : "mms");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json;charset=UTF-8");
        headers.set("X-Secret-Key", secretKey);
        List<Map<String, Object>> recipientList = List.of(Map.of("recipientNo", receiveTel));
        Map<String, Object> map = new HashMap<>();
        if (title != null) map.put("title", title);
        map.put("body", content);
        map.put("sendNo", sendTel);
        map.put("recipientList", recipientList);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(map, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }
}
