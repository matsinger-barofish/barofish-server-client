package com.matsinger.barofishserver.siteInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SiteInfoService {

    private final SiteInfoRepository siteInfoRepository;

    public List<SiteInformation> selectSiteInfoList() {
        return siteInfoRepository.findAllByIdNotContaining("INTERNAL");
    }

    public SiteInformation selectSiteInfo(String id) {
        return siteInfoRepository.findById(id).orElseThrow(() -> {
            throw new Error("정보를 찾을 수 없습니다.");
        });
    }

    public SiteInformation updateSiteInfo(SiteInformation siteInfo) {
        return siteInfoRepository.save(siteInfo);
    }
}
