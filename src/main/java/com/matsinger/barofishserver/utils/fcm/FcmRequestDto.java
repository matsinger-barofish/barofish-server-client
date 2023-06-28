package com.matsinger.barofishserver.utils.fcm;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmRequestDto {
    private Integer targetUserId;
    private String title;
    private String body;
    private Map<String ,String> data;
}
