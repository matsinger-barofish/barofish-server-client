package com.matsinger.barofishserver.siteInfo.application;

import com.matsinger.barofishserver.siteInfo.domain.SiteInformation;
import com.matsinger.barofishserver.siteInfo.repository.SiteInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SiteInfoCommandService {
    private final SiteInfoRepository siteInfoRepository;
    public SiteInformation updateSiteInfo(SiteInformation siteInfo) {
        return siteInfoRepository.save(siteInfo);
    }

}
