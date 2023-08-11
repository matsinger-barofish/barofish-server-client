package com.matsinger.barofishserver.productinfonotice.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class productInfoReq {

    private int productId;
    private int code;
    private Map<String, String> data = new HashMap<>();
}
