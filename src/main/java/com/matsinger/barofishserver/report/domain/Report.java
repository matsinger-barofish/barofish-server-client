package com.matsinger.barofishserver.report.domain;

import com.matsinger.barofishserver.report.dto.ReportDto;
import com.matsinger.barofishserver.review.domain.Review;
import com.matsinger.barofishserver.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Time;
import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "report", schema = "barofish_dev", catalog = "")
public class Report {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "user_id", nullable = false)
    private int userId;

    @ManyToOne
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private User user;

    @Basic
    @Column(name = "review_id", nullable = false)
    private int reviewId;

    @ManyToOne
    @JoinColumn(name = "review_id", updatable = false, insertable = false)
    private Review review;

    @Basic
    @Column(name = "content", nullable = false)
    private String content;

    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Basic
    @Column(name = "confirm_at", nullable = true)
    private Timestamp confirmAt;

    public void setConfirmAt(Timestamp confirmAt) {
        this.confirmAt = confirmAt;
    }

    public ReportDto convert2Dto() {
        return ReportDto.builder().id(this.id).content(this.content).createdAt(this.createdAt).confirmAt(this.confirmAt).build();
    }
}
