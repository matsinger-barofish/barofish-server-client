package com.matsinger.barofishserver.domain.data.tip.domain;

public enum TipOrderBy {
    id("id"), type("type"), title("title"), description("description"), createdAt("createdAt");

    public final String label;

    TipOrderBy(String label) {
        this.label = label;
    }
}
