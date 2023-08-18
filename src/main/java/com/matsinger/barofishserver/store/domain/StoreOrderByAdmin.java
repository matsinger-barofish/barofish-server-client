package com.matsinger.barofishserver.store.domain;

public enum StoreOrderByAdmin {
    id("id"), state("state"), loginId("loginId"), name("storeInfo.name"), location("storeInfo.location"), joinAt(
            "joinAt"), isReliable("storeInfo.isReliable");


    public final String label;

    StoreOrderByAdmin(String label) {
        this.label = label;
    }
}
