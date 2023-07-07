package com.matsinger.barofishserver.store.object;

public enum StoreOrderByAdmin {
    id("id"), state("state"), loginId("loginId"), name("storeInfo.name"), location("storeInfo.location"), joinAt(
            "joinAt");


    public final String label;

    StoreOrderByAdmin(String label) {
        this.label = label;
    }
}
