package com.matsinger.barofishserver.domain.siteInfo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SiteInfoReq {
    String content;
    List<TitleContentReq> tcContent;
}
