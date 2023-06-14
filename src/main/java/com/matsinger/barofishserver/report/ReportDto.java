package com.matsinger.barofishserver.report;

import com.matsinger.barofishserver.review.ReviewDto;
import com.matsinger.barofishserver.user.object.UserDto;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportDto {
    Integer id;
    UserDto user;
    ReviewDto review;
    String content;
    Timestamp createdAt;
    Timestamp confirmAt;
}
