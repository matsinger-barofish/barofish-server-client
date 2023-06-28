package com.matsinger.barofishserver.banner;

public enum BannerOrderBy {
    id("id"), type("type"), curationId("curationId"), noticeId("noticeId"), categoryId("categoryId");

    public final String label;

    private BannerOrderBy(String label) {
        this.label = label;
    }
}
