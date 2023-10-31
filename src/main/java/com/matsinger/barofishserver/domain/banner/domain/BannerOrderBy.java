package com.matsinger.barofishserver.domain.banner.domain;

public enum BannerOrderBy {
    id("id"), type("type"), curationId("curationId"), noticeId("noticeId"), categoryId("categoryId"), sortNo("sortNo");

    public final String label;

    BannerOrderBy(String label) {
        this.label = label;
    }
}
