package com.matsinger.barofishserver.admin.log.domain;

import com.matsinger.barofishserver.admin.log.dto.AdminLogDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLog {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    @Basic
    @Column(name = "admin_id")
    private Integer adminId;
    @Basic
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdminLogType type;
    @Basic
    @Column(name = "target_id", nullable = false)
    private String targetId;
    @Basic
    @Column(name = "content", nullable = false, length = 300)
    private String content;
    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;

    public AdminLogDto convert2Dto() {
        return AdminLogDto.builder().id(this.getId()).type(this.getType()).targetId(this.getTargetId()).content(this.getContent()).createdAt(
                this.getCreatedAt()).build();
    }
}
