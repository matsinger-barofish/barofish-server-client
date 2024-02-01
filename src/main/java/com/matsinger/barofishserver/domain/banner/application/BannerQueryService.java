package com.matsinger.barofishserver.domain.banner.application;

import com.matsinger.barofishserver.domain.banner.domain.Banner;
import com.matsinger.barofishserver.domain.banner.domain.BannerOrderBy;
import com.matsinger.barofishserver.domain.banner.domain.BannerState;
import com.matsinger.barofishserver.domain.banner.domain.BannerType;
import com.matsinger.barofishserver.domain.banner.dto.BannerDto;
import com.matsinger.barofishserver.domain.banner.repository.BannerQueryRepository;
import com.matsinger.barofishserver.domain.banner.repository.BannerRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class BannerQueryService {
    private final BannerRepository bannerRepository;
    private final BannerQueryRepository bannerQueryRepository;

    public List<Banner> selectBannerListWithIds(List<Integer> ids) {
        return bannerRepository.findAllByIdIn(ids);
    }

    public List<Banner> selectBannerList() {
        return bannerRepository.findAllByStateEqualsAndTypeIn(BannerState.ACTIVE,
                Arrays.asList(BannerType.NONE, BannerType.CATEGORY, BannerType.CURATION, BannerType.NOTICE));
    }

    public List<Banner> selectBannersOrderBySortNum() {
        return bannerRepository.findAllByStateAndTypeInOrderBySortNoAsc(BannerState.ACTIVE,
                Arrays.asList(BannerType.NONE, BannerType.CATEGORY, BannerType.CURATION, BannerType.NOTICE));
    }

    public List<Banner> selectBannerListByAdmin(Specification<Banner> spec) {
        return bannerRepository.findAll(spec);
    }

    public List<Banner> selectBannerListWithSortNo() {
        return bannerRepository.findAllByTypeInOrderBySortNoAsc(List.of(BannerType.NONE,
                BannerType.NOTICE,
                BannerType.CURATION,
                BannerType.CATEGORY));
    }

    public List<Banner> selectMainBanner() {
        return bannerRepository.findAllByTypeAndState(BannerType.MAIN, BannerState.ACTIVE);
    }

    public Banner selectPcWebBanner() {
        return bannerRepository.findFirstByTypeAndState(BannerType.PC_WEB, BannerState.ACTIVE);
    }

    public Banner selectBanner(Integer id) {
        return bannerRepository.findById(id).orElseThrow(() -> {
            throw new BusinessException("배너 정보를 찾을 수 없습니다.");
        });
    }

    public List<Banner> selectMyPageBanner() {
        return bannerRepository.findAllByTypeAndState(BannerType.MY_PAGE, BannerState.ACTIVE);
    }

    public Integer getSortNo() {
        Banner
                banner =
                bannerRepository.findFirstByTypeInOrderBySortNoDesc(List.of(BannerType.NONE,
                        BannerType.NOTICE,
                        BannerType.CATEGORY,
                        BannerType.CURATION));
        if (banner != null) return banner.getSortNo() + 1;
        else return 1;
    }

    public Banner findById(Integer bannerId) {
        return bannerRepository.findById(bannerId)
                .orElseThrow(() -> new BusinessException("배너 정보를 찾을 수 없습니다."));
    }

    public List<BannerDto> selectBannersByAdmin(Pageable pageable,
                                                String types,
                                                BannerOrderBy orderBy,
                                                Sort.Direction sort) {

        List<BannerType> bannerTypes = Arrays.stream(types.split(",")).map(BannerType::valueOf).toList();
        return bannerQueryRepository.selectBannersByAdmin(
                pageable,
                bannerTypes,
                orderBy,
                sort
        );
    }
}
