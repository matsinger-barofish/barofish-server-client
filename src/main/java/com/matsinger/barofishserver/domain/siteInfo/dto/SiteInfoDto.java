package com.matsinger.barofishserver.domain.siteInfo.dto;

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
