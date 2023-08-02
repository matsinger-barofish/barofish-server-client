package com.matsinger.barofishserver.banner.application;

import com.matsinger.barofishserver.banner.domain.Banner;
import com.matsinger.barofishserver.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class BannerCommandService {
    private final BannerRepository bannerRepository;

    public Banner addBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    public void updateAllBanners(List<Banner> banners) {
        bannerRepository.saveAll(banners);
    }

    public Banner updateBanner(Banner banner) {
        return bannerRepository.save(banner);
    }

    public Boolean deleteBanner(Integer id) {
        try {
            bannerRepository.deleteById(id);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
