package com.matsinger.barofishserver.domain.data.curation.domain;

public enum CurationOrderBy {
    id("id"), sortNo("sortNo"), shortName("shortName"), title("title"), description("description"), type("type");

    public final String label;

    CurationOrderBy(String label) {
        this.label = label;
    }
}
