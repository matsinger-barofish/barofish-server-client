package com.matsinger.barofishserver.siteInfo.repository;

import com.matsinger.barofishserver.siteInfo.domain.SiteInformation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiteInfoRepository extends JpaRepository<SiteInformation, String> {
    List<SiteInformation> findAllByIdNotContaining(String internal);
}
