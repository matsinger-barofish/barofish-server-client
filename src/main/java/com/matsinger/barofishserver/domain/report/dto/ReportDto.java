package com.matsinger.barofishserver.domain.report.dto;

import com.matsinger.barofishserver.domain.review.dto.ReviewDto;
import com.matsinger.barofishserver.domain.userinfo.dto.UserInfoDto;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {
    Integer id;
    UserInfoDto user;

    ReviewDto review;
    String content;
    Timestamp createdAt;
    Timestamp confirmAt;
}
