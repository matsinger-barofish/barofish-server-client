package com.matsinger.barofishserver.report;

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

    @Basic
    @Column(name = "review_id", nullable = false)
    private int reviewId;

    @Basic
    @Column(name = "content", nullable = false)
    private String content;

    @Basic
    @Column(name = "created_at", nullable = false)
    @ColumnDefault("CURRENT_TIMESTAMP()")
    private Timestamp createdAt;

    @Basic
    @Column(name = "confirm_at", nullable = true)
    private Timestamp confirmAt;

    ReportDto convert2Dto() {
        return ReportDto.builder().id(this.id).content(this.content).createdAt(this.createdAt).confirmAt(this.confirmAt).build();
    }
}
