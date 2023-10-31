package com.matsinger.barofishserver.domain.banner.repository;

import com.matsinger.barofishserver.domain.banner.domain.BannerState;
import com.matsinger.barofishserver.domain.banner.domain.BannerType;
import com.matsinger.barofishserver.domain.banner.domain.Banner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer>, JpaSpecificationExecutor<Banner> {
    List<Banner> findAllByStateEqualsAndTypeIn(BannerState state, List<BannerType> bannerTypes);

    List<Banner> findAllByStateEquals(BannerState state);

    Banner findFirstByTypeAndState(BannerType type, BannerState state);

    List<Banner> findAllByTypeAndState(BannerType type, BannerState state);

    List<Banner> findAllByIdIn(List<Integer> ids);

    Banner findFirstByTypeInOrderBySortNoDesc(List<BannerType> type);

    @Query(value = "select b from Banner b")
    List<Banner> findWithPagination(Pageable pageable);

    List<Banner> findAllByTypeInOrderBySortNoAsc(List<BannerType> type);
}
