package com.matsinger.barofishserver.domain.searchFilter.repository;

import com.matsinger.barofishserver.domain.searchFilter.domain.SearchFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchFilterRepository extends JpaRepository<SearchFilter, Integer> {

}
