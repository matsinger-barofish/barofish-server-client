package com.matsinger.barofishserver.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class CustomResponse {
    private Boolean isSuccess = true;
    private String ErrorMsg = null;
    private Optional<Object> data;
}
