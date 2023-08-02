package com.matsinger.barofishserver.siteInfo.dto;

import com.matsinger.barofishserver.siteInfo.api.SiteInfoController;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SiteInfoReq {
    String content;
    List<TitleContentReq> tcContent;
}
