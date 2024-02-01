package com.matsinger.barofishserver.domain.admin.log.dto;

import com.matsinger.barofishserver.domain.admin.log.domain.AdminLogType;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;

@Getter
@Builder
public class AdminLogDto {
    private String id;
    private AdminLogType type;
    private String targetId;
    private String content;
    private Timestamp createdAt;
}
