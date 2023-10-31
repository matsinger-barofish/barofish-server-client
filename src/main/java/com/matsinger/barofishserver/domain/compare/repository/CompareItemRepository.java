package com.matsinger.barofishserver.domain.compare.repository;

import com.matsinger.barofishserver.domain.compare.domain.CompareItem;
import com.matsinger.barofishserver.domain.compare.domain.CompareItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompareItemRepository extends JpaRepository<CompareItem, CompareItemId> {
    public void deleteByCompareSetId(Integer compareId);

    public List<CompareItem> findAllByCompareSetId(Integer compareSetId);
    void deleteAllByCompareSetIdIn(List<Integer> ids);
}
