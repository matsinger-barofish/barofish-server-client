package com.matsinger.barofishserver.searchFilter.application;

import com.matsinger.barofishserver.searchFilter.domain.SearchFilter;
import com.matsinger.barofishserver.searchFilter.domain.SearchFilterField;
import com.matsinger.barofishserver.searchFilter.repository.ProductSearchFilterMapRepository;
import com.matsinger.barofishserver.searchFilter.repository.SearchFilterFieldRepository;
import com.matsinger.barofishserver.searchFilter.repository.SearchFilterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchFilterQueryService {
    private final SearchFilterRepository searchFilterRepository;
    private final SearchFilterFieldRepository searchFilterFieldRepository;
    private final ProductSearchFilterMapRepository productSearchFilterMapRepository;
    public List<SearchFilter> selectSearchFilterList() {
        return searchFilterRepository.findAll();
    }

    public SearchFilter selectSearchFilter(Integer id) {
        return searchFilterRepository.findById(id).orElseThrow(() -> {
            throw new Error("검색 필터 정보를 찾을 수 없습니다.");
        });
    }
    public List<SearchFilterField> selectSearchFilterFieldWithFilterId(Integer id) {
        return searchFilterFieldRepository.findAllBySearchFilterId(id);
    }

    public SearchFilterField selectSearchFilterField(Integer id) {
        return searchFilterFieldRepository.findById(id).orElseThrow(() -> {
            throw new Error("검색 필터 필드 정보를 찾을 수 없습니다.");
        });
    }
}
