package com.matsinger.barofishserver.domain.notice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@Table(name = "notice", schema = "barofish_dev", catalog = "")
public class Notice {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NoticeType type;
    @Basic
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    @Basic
    @Column(name = "content", nullable = false, length = -1)
    private String content;
    @Basic
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @Basic
    @Column(name = "update_at", nullable = true)
    private Timestamp updateAt;

    @Basic
    @Column(name = "is_representative", nullable = false)
    private boolean isRepresentative;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUpdateAt(Timestamp updateAt) {
        this.updateAt = updateAt;
    }
}
