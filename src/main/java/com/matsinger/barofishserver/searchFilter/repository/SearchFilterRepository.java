package com.matsinger.barofishserver.searchFilter.repository;

import com.matsinger.barofishserver.searchFilter.domain.SearchFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchFilterRepository extends JpaRepository<SearchFilter, Integer> {

}
