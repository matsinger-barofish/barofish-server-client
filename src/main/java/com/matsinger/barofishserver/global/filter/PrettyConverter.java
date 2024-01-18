package com.matsinger.barofishserver.global.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class PrettyConverter implements JsonViewConverters {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final JsonParser parser = new JsonParser();
    @Override
    public String convert(byte[] obj) {
        return gson.toJson(parser.parse(new String(obj, StandardCharsets.UTF_8)));
    }
}
