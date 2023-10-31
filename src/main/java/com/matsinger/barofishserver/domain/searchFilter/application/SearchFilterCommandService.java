package com.matsinger.barofishserver.domain.searchFilter.application;

import com.matsinger.barofishserver.domain.searchFilter.domain.SearchFilter;
import com.matsinger.barofishserver.domain.searchFilter.domain.SearchFilterField;
import com.matsinger.barofishserver.domain.searchFilter.dto.SearchFilterDto;
import com.matsinger.barofishserver.domain.searchFilter.repository.ProductSearchFilterMapRepository;
import com.matsinger.barofishserver.domain.searchFilter.repository.SearchFilterFieldRepository;
import com.matsinger.barofishserver.domain.searchFilter.repository.SearchFilterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchFilterCommandService {
    private final SearchFilterRepository searchFilterRepository;
    private final SearchFilterFieldRepository searchFilterFieldRepository;
    private final ProductSearchFilterMapRepository productSearchFilterMapRepository;

    public SearchFilterDto convertFilterDto(SearchFilter filter) {
        return SearchFilterDto.builder().id(filter.getId()).name(filter.getName()).searchFilterFields(
                searchFilterFieldRepository.findAllBySearchFilterId(filter.getId()).stream().map(SearchFilterField::convert2Dto).toList()).build();
    }

    public SearchFilter addSearchFilter(SearchFilter filter) {
        return searchFilterRepository.save(filter);
    }

    public SearchFilter updateSearchFilter(SearchFilter filter) {
        return searchFilterRepository.save(filter);
    }

    @Transactional
    public void deleteSearchFilter(Integer id) {
        List<SearchFilterField> searchFilterFields = searchFilterFieldRepository.findAllBySearchFilterId(id);
        productSearchFilterMapRepository.deleteAllByFieldIdIn(searchFilterFields.stream().mapToInt(SearchFilterField::getId).boxed().toList());
        searchFilterFieldRepository.deleteAllBySearchFilterId(id);
        searchFilterRepository.deleteById(id);
    }

    public SearchFilterField addSearchFilterField(SearchFilterField field) {
        return searchFilterFieldRepository.save(field);
    }

    public SearchFilterField updateSearchFilterField(SearchFilterField field) {
        return searchFilterFieldRepository.save(field);
    }

    @Transactional
    public void deleteSearchFilterField(Integer id) {
        productSearchFilterMapRepository.deleteAllByFieldIdIn(new ArrayList<>(Collections.singletonList(id)));
        searchFilterFieldRepository.deleteById(id);
    }
}
