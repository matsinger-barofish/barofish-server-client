package com.matsinger.barofishserver.domain.compare.recommend.application;

import com.matsinger.barofishserver.domain.compare.domain.SaveProductId;
import com.matsinger.barofishserver.domain.compare.recommend.domain.RecommendCompareSetType;
import com.matsinger.barofishserver.domain.compare.recommend.domain.RecommendCompareSet;
import com.matsinger.barofishserver.domain.compare.recommend.dto.RecommendCompareSetDto;
import com.matsinger.barofishserver.domain.compare.recommend.repository.RecommendCompareSetRepository;
import com.matsinger.barofishserver.domain.compare.repository.SaveProductRepository;
import com.matsinger.barofishserver.domain.product.application.ProductService;
import com.matsinger.barofishserver.domain.product.dto.ProductListDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class RecommendCompareSetService {
    private final RecommendCompareSetRepository recommendCompareSetRepository;
    private final ProductService productService;
    private final SaveProductRepository saveProductRepository;


    public RecommendCompareSet addRecommendCompareSet(RecommendCompareSet compareSet) {
        return recommendCompareSetRepository.save(compareSet);
    }

    public RecommendCompareSet updateRecommendCompareSet(RecommendCompareSet compareSet) {
        return recommendCompareSetRepository.save(compareSet);
    }

    public void deleteRecommendCompareSetWithProductId(Integer productId) {
        recommendCompareSetRepository.deleteAllByProduct1IdOrProduct2IdOrProduct3Id(productId, productId, productId);
    }

    public List<RecommendCompareSet> selectRecommendCompareSetList(RecommendCompareSetType type) {
        return recommendCompareSetRepository.findAllByType(type);
    }

    public List<RecommendCompareSet> selectRecommendCompareSetByRandom() {
        return recommendCompareSetRepository.findAllByTypeRandom();
    }

    public RecommendCompareSetDto convert2Dto(RecommendCompareSet set, Integer userId) {
        List<ProductListDto>
                productListDtos =
                new ArrayList<>(Arrays.asList(set.getProduct1Id(),
                        set.getProduct2Id(),

                        set.getProduct3Id())).stream().map(v -> {
                    ProductListDto dto = productService.convert2ListDto(productService.selectProduct(v));
                    if (userId != null) dto.setIsLike(saveProductRepository.existsById(new SaveProductId(userId, v)));
                    return dto;
                }).toList();

        return RecommendCompareSetDto.builder().id(set.getId()).type(set.getType()).products(productListDtos).build();
    }

    public RecommendCompareSet selectRecommendCompareSet(Integer id) {
        return recommendCompareSetRepository.findById(id).orElseThrow(() -> {
            throw new Error("추천 비교하기 세트 정보를 찾을 수 없습니다.");
        });
    }

    public void deleteRecommendCompareSet(Integer id) {
        recommendCompareSetRepository.deleteById(id);
    }
}
