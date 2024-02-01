package com.matsinger.barofishserver.domain.siteInfo.application;

import com.matsinger.barofishserver.domain.siteInfo.domain.SiteInformation;
import com.matsinger.barofishserver.domain.siteInfo.repository.SiteInfoRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SiteInfoQueryService {
    private final SiteInfoRepository siteInfoRepository;
    public List<SiteInformation> selectSiteInfoList() {
        return siteInfoRepository.findAllByIdNotContaining("INTERNAL");
    }

    public SiteInformation selectSiteInfo(String id) {
        return siteInfoRepository.findById(id).orElseThrow(() -> {
            throw new BusinessException("정보를 찾을 수 없습니다.");
        });
    }
}
