package com.matsinger.barofishserver.domain.siteInfo.application;

import com.matsinger.barofishserver.domain.siteInfo.domain.SiteInformation;
import com.matsinger.barofishserver.domain.siteInfo.repository.SiteInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class SiteInfoCommandService {
    private final SiteInfoRepository siteInfoRepository;
    public SiteInformation updateSiteInfo(SiteInformation siteInfo) {
        return siteInfoRepository.save(siteInfo);
    }

}
