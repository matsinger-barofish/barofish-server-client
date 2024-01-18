package com.matsinger.barofishserver.domain.compare.recommend.repository;

import com.matsinger.barofishserver.domain.compare.recommend.domain.RecommendCompareSetType;
import com.matsinger.barofishserver.domain.compare.recommend.domain.RecommendCompareSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RecommendCompareSetRepository extends JpaRepository<RecommendCompareSet, Integer> {
    List<RecommendCompareSet> findAllByType(RecommendCompareSetType type);

    void deleteAllByProduct1IdOrProduct2IdOrProduct3Id(Integer product1Id, Integer product2Id, Integer product3Id);

    @Query(value = "select * from recommend_compare_set where type = 'RECOMMEND' order by rand() limit 5", nativeQuery = true)
    List<RecommendCompareSet> findAllByTypeRandom();


}
