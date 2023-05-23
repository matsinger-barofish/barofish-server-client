package com.matsinger.barofishserver.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentQueryService {

    private final String identificationCode = "imp38983932";
    private final String impKey = "8069640719378424";
    private final String impSecret = "9163909ecb98ff8fc1e0ced24d3ed59583f740d446bd8be477ed1e3110032b54f610f664c2a1e81e";

    public Map<String, String> getKeys() {
        Map<String, String> keys = new HashMap<>();
        keys.put("identificationCode", identificationCode);
        keys.put("impKey", impKey);
        keys.put("impSecret", impSecret);
        return keys;
    }
}
