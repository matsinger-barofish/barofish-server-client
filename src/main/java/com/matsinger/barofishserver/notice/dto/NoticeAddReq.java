package com.matsinger.barofishserver.notice.dto;

import com.matsinger.barofishserver.notice.domain.NoticeType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeAddReq {
    NoticeType type;
    String title;
    String content;
}
