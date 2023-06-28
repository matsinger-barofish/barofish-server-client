package com.matsinger.barofishserver.banner;

import jakarta.persistence.*;
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
    private String link;
    private Integer curationId;
    private String curationName;
    private Integer noticeId;
    private String noticeTitle;
    private Integer categoryId;
    private String categoryName;

}
