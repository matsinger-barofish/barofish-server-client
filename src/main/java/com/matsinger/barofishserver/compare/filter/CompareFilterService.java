package com.matsinger.barofishserver.compare.filter;

import com.matsinger.barofishserver.category.CategoryFilterRepository;
import com.matsinger.barofishserver.product.filter.ProductFilterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CompareFilterService {
    private final CompareFilterRepository compareFilterRepository;
    private final ProductFilterRepository productFilterRepository;
    private final CategoryFilterRepository categoryFilterRepository;

    public CompareFilter addCompareFilter(CompareFilter compareFilter) {
        return compareFilterRepository.save(compareFilter);
    }

    public CompareFilter updateCompareFilter(CompareFilter compareFilter) {
        return compareFilterRepository.save(compareFilter);
    }

    public void deleteCompareFilter(Integer filterId) {
        productFilterRepository.deleteAllByCompareFilterId(filterId);
        categoryFilterRepository.deleteAllByCompareFilterId(filterId);
        compareFilterRepository.deleteById(filterId);
    }

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
