package com.matsinger.barofishserver.monitoring;

import lombok.Getter;

@Getter
public class TestRequest {

    private Integer id;
    private String name;

    @Override
    public String toString() {
        return "TestRequest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
