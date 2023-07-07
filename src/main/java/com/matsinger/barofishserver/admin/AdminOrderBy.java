package com.matsinger.barofishserver.admin;

public enum AdminOrderBy {
    id("id"), state("state"), name("name"), tel("tel"), createdAt("createdAt");

    public final String label;

    AdminOrderBy(String label) {
        this.label = label;
    }
}
