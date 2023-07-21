package com.matsinger.barofishserver.product.productfilter.application;

import com.matsinger.barofishserver.compare.filter.application.CompareFilterQueryService;
import com.matsinger.barofishserver.compare.filter.domain.CompareFilter;
import com.matsinger.barofishserver.product.productfilter.repository.ProductFilterRepository;
import com.matsinger.barofishserver.product.productfilter.dto.ProductFilterValueDto;
import com.matsinger.barofishserver.product.productfilter.domain.ProductFilterValue;
import com.matsinger.barofishserver.product.productfilter.domain.ProductFilterValueId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProductFilterService {
    private final ProductFilterRepository productFilterRepository;
    private final CompareFilterQueryService compareFilterQueryService;

    public void addAllProductFilter(List<ProductFilterValue> values) {
        productFilterRepository.saveAll(values);
    }

    public Optional<ProductFilterValue> selectProductFilterValue(ProductFilterValueId valueId) {
        return productFilterRepository.findById(valueId);
    }

    @Transactional
    public void deleteAllFilterValueWithProductId(Integer productId) {
        productFilterRepository.deleteAllByProductId(productId);
    }

    public List<ProductFilterValueDto> selectProductFilterValueListWithProductId(Integer productId) {
        return productFilterRepository.findAllByProductId(productId).stream().map(this::convert2Dto).toList();
    }

    public ProductFilterValueDto convert2Dto(ProductFilterValue value) {

        CompareFilter filter = compareFilterQueryService.selectCompareFilter(value.getCompareFilterId());
        return ProductFilterValueDto.builder().compareFilterId(filter.getId()).compareFilterName(filter.getName())
                .value(
                        value.getValue())
                .build();
    }

}
