package com.matsinger.barofishserver.banner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class BannerService {
    @Autowired
    private final BannerRepository bannerRepository;

    public Banner add(Banner banner) {
        return bannerRepository.save(banner);
    }

    public List<Banner> selectBannerList() {
        return bannerRepository.findAllByStateEquals(BannerState.ACTIVE);

    }

    public Banner selectBanner(Integer id) {
        return bannerRepository.findById(id).orElseThrow(() -> {
            throw new Error("배너 정보를 찾을 수 없습니다.");
        });
    }
}
