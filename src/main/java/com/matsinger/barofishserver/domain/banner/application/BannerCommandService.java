package com.matsinger.barofishserver.domain.banner.application;

import com.matsinger.barofishserver.domain.banner.domain.Banner;
import com.matsinger.barofishserver.domain.banner.dto.BannerDto;
import com.matsinger.barofishserver.domain.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Transactional
    public List<BannerDto> sortBanners(List<Integer> bannerIds) {
        List<Banner> banners = bannerRepository.findAll();
        for (Banner banner : banners) {
            banner.setSortNo(null);
        }

        List<BannerDto> bannerDtos = new ArrayList<>();
        List<Banner> bannersToSort = bannerRepository.findAllByIdIn(bannerIds);
        int sortIdx = 1;
        for (Banner banner : bannersToSort) {
            banner.setSortNo(sortIdx);
            bannerDtos.add(banner.convert2Dto());
            sortIdx++;
        }
        return bannerDtos;
    }
}
