package com.matsinger.barofishserver.report;

import com.matsinger.barofishserver.review.object.ReviewDto;
import com.matsinger.barofishserver.user.object.UserInfoDto;
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
