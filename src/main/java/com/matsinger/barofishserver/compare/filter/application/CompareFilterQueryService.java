package com.matsinger.barofishserver.compare.filter.application;

import com.matsinger.barofishserver.category.filter.repository.CategoryFilterRepository;
import com.matsinger.barofishserver.compare.filter.domain.CompareFilter;
import com.matsinger.barofishserver.compare.filter.repository.CompareFilterRepository;
import com.matsinger.barofishserver.product.productfilter.repository.ProductFilterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CompareFilterQueryService {
    private final CompareFilterRepository compareFilterRepository;
    private final ProductFilterRepository productFilterRepository;
    private final CategoryFilterRepository categoryFilterRepository;

    public CompareFilter selectCompareFilter(Integer filterId) {
        return compareFilterRepository.findById(filterId).orElseThrow(() -> {
            throw new Error("비교하기 항목 정보를 찾을 수 없습니다.");
        });
    }

    public List<CompareFilter> selectCompareFilterList() {
        return compareFilterRepository.findAll();
    }

    public List<CompareFilter> selectCompareFilterListWithIds(List<Integer> ids) {
        return compareFilterRepository.findAllByIdIn(ids);
    }
}
