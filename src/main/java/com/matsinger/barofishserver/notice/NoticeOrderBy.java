package com.matsinger.barofishserver.notice;

public enum NoticeOrderBy {
    type("type"),
    title("title"),
    createdAt("createdAt");

    public final String label;

    private NoticeOrderBy(String label){
        this.label=label;
    }
}
