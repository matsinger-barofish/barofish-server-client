package com.matsinger.barofishserver.searchFilter.repository;

import com.matsinger.barofishserver.searchFilter.domain.SearchFilterField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchFilterFieldRepository extends JpaRepository<SearchFilterField, Integer> {
    List<SearchFilterField> findAllBySearchFilterId(Integer filterId);

    void deleteAllBySearchFilterId(Integer filterId);
}