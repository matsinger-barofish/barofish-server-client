package com.matsinger.barofishserver.siteInfo.dto;

import com.matsinger.barofishserver.siteInfo.api.SiteInfoController;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SiteInfoDto {
    private String id;
    private String description;
    private String content;
    private List<TitleContentReq> tcContent;

}
