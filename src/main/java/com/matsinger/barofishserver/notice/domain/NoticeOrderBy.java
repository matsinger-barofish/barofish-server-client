package com.matsinger.barofishserver.notice.domain;

public enum NoticeOrderBy {
    type("type"), title("title"), createdAt("createdAt");

    public final String label;

    NoticeOrderBy(String label) {
        this.label = label;
    }
}
