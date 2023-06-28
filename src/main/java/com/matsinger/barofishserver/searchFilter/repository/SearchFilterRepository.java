package com.matsinger.barofishserver.searchFilter.repository;

import com.matsinger.barofishserver.searchFilter.object.SearchFilter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchFilterRepository extends JpaRepository<SearchFilter,Integer> {

}
