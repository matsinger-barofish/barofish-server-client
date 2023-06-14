package com.matsinger.barofishserver.siteInfo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteInfoRepository extends JpaRepository<SiteInformation, String> {
    List<SiteInformation> findAllByIdNotContaining(String internal);
}
