package com.matsinger.barofishserver.report;

import com.matsinger.barofishserver.review.object.Review;
import com.matsinger.barofishserver.user.object.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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

    ReportDto convert2Dto() {
        return ReportDto.builder().id(this.id).content(this.content).createdAt(this.createdAt).confirmAt(this.confirmAt).build();
    }
}
