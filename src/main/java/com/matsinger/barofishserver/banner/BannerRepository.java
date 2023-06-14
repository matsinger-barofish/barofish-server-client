package com.matsinger.barofishserver.banner;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Integer> {
    public List<Banner> findAllByStateEqualsAndTypeIn(BannerState state, List<BannerType> bannerTypes);

    public List<Banner> findAllByStateEquals(BannerState state);

    public Banner findFirstByTypeAndState(BannerType type, BannerState state);

    @Query("select b from Banner b")
    List<Banner> findWithPagination(Pageable pageable);
}
