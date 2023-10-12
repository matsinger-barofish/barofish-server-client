package com.matsinger.barofishserver.product.holiday;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class Holidays {

    private int totalCount;
    private List<Holiday> holidays;

    @Override
    public String toString() {
        return "Holidays{" +
                "totalCount=" + totalCount +
                ", holidays=" + holidays +
                '}';
    }
}
