package com.matsinger.barofishserver.domain.store.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class StoreExcelDownloadReq {

    private List<Integer> storeIds;
}
