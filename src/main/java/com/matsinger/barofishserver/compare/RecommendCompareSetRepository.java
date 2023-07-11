package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.compare.obejct.RecommendCompareSet;
import com.matsinger.barofishserver.compare.obejct.RecommendCompareSetType;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecommendCompareSetRepository extends JpaRepository<RecommendCompareSet, Integer> {
    List<RecommendCompareSet> findAllByType(RecommendCompareSetType type);

    void deleteAllByProduct1IdOrProduct2IdOrProduct3Id(Integer product1Id, Integer product2Id, Integer product3Id);

    @Query(value = "select * from recommend_compare_set where type = 'RECOMMEND' order by rand() limit 5", nativeQuery = true)
    List<RecommendCompareSet> findAllByTypeRandom();


}
