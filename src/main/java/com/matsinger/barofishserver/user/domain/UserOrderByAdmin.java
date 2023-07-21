package com.matsinger.barofishserver.user.domain;

public enum UserOrderByAdmin {
    state("user.state"), grade("grade"), name("name"), nickname("nickname"), email("email"), phone("phone"),point(
            "point"),
    joinAt(
            "user.joinAt");

    public final String label;

    private UserOrderByAdmin(String label) {
        this.label = label;
    }
}
