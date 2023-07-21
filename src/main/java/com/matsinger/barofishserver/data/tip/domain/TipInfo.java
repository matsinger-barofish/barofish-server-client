package com.matsinger.barofishserver.data.tip.domain;

import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipInfo {
    String thumbnailImage;
    String name;
    String title;
    String subTitle;
}
