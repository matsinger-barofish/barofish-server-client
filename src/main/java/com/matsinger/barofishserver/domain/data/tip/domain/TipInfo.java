package com.matsinger.barofishserver.domain.data.tip.domain;

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
