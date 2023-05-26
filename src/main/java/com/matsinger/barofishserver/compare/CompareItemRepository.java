package com.matsinger.barofishserver.compare;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompareItemRepository extends JpaRepository<CompareItem, CompareItemId> {
    public void deleteByCompareSetId(Integer compareId);

    public List<CompareItem> findAllByCompareSetId(Integer compareSetId);
}
