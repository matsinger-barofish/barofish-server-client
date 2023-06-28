package com.matsinger.barofishserver.compare.filter;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompareFilterRepository extends JpaRepository<CompareFilter, Integer> {
    List<CompareFilter> findAllByIdIn(List<Integer> ids);
}
