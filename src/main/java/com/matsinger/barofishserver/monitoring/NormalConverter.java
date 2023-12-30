package com.matsinger.barofishserver.monitoring;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class NormalConverter implements JsonViewConverters {


    @Override
    public String convert(byte[] obj) {
        return new String(obj, StandardCharsets.UTF_8);
    }
}
