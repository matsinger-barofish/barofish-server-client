package com.matsinger.barofishserver.domain.report.domain;

public enum ReportOrderBy {
    userName("user.userInfo.name"), userEmail("user.userInfo.email"), targetName("review.user.user.name"), targetEmail(
            "review.user.userInfo.email"), createdAt("createdAt"), confirmAt("confirmAt");
    public final String label;

    ReportOrderBy(String label) {
        this.label = label;
    }

}
