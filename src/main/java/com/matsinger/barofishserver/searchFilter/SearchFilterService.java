package com.matsinger.barofishserver.searchFilter;

import com.matsinger.barofishserver.searchFilter.object.SearchFilter;
import com.matsinger.barofishserver.searchFilter.object.SearchFilterDto;
import com.matsinger.barofishserver.searchFilter.object.SearchFilterField;
import com.matsinger.barofishserver.searchFilter.object.SearchFilterFieldDto;
import com.matsinger.barofishserver.searchFilter.repository.ProductSearchFilterMapRepository;
import com.matsinger.barofishserver.searchFilter.repository.SearchFilterFieldRepository;
import com.matsinger.barofishserver.searchFilter.repository.SearchFilterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class SearchFilterService {
    private final SearchFilterRepository searchFilterRepository;
    private final SearchFilterFieldRepository searchFilterFieldRepository;
    private final ProductSearchFilterMapRepository productSearchFilterMapRepository;

    public SearchFilterDto convertFilterDto(SearchFilter filter) {
        return SearchFilterDto.builder().id(filter.getId()).name(filter.getName()).searchFilterFields(
                selectSearchFilterFieldWithFilterId(filter.getId()).stream().map(SearchFilterField::convert2Dto).toList()).build();
    }

    //SearchFilter
    public List<SearchFilter> selectSearchFilterList() {
        return searchFilterRepository.findAll();
    }

    public SearchFilter selectSearchFilter(Integer id) {
        return searchFilterRepository.findById(id).orElseThrow(() -> {
            throw new Error("검색 필터 정보를 찾을 수 없습니다.");
        });
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


    //SearchFilterField
    public List<SearchFilterField> selectSearchFilterFieldWithFilterId(Integer id) {
        return searchFilterFieldRepository.findAllBySearchFilterId(id);
    }

    public SearchFilterField selectSearchFilterField(Integer id) {
        return searchFilterFieldRepository.findById(id).orElseThrow(() -> {
            throw new Error("검색 필터 필드 정보를 찾을 수 없습니다.");
        });
    }

    public SearchFilterField addSearchFilterField(SearchFilterField field) {
        return searchFilterFieldRepository.save(field);
    }

    public SearchFilterField updateSearchFilterField(SearchFilterField field) {
        return searchFilterFieldRepository.save(field);
    }

    @Transactional
    public void deleteSearchFilterField(Integer id) {
        productSearchFilterMapRepository.deleteAllByFieldIdIn(new ArrayList<>(Arrays.asList(id)));
        searchFilterFieldRepository.deleteById(id);
    }
}
