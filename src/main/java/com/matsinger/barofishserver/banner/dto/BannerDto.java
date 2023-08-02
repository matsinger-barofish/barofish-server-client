package com.matsinger.barofishserver.banner.dto;

import com.matsinger.barofishserver.banner.domain.BannerState;
import com.matsinger.barofishserver.banner.domain.BannerType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerDto {
    private int id;
    private BannerState state;
    private BannerType type;
    private String image;
    private Integer sortNo;
    private String link;
    private Integer curationId;
    private String curationName;
    private Integer noticeId;
    private String noticeTitle;
    private Integer categoryId;
    private String categoryName;

}
