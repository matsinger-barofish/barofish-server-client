package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.compare.obejct.RecommendCompareSet;
import com.matsinger.barofishserver.compare.obejct.RecommendCompareSetDto;
import com.matsinger.barofishserver.compare.obejct.RecommendCompareSetType;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.ProductListDto;
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

    public RecommendCompareSetDto convert2Dto(RecommendCompareSet set) {
        List<ProductListDto>
                productListDtos =
                new ArrayList<>(Arrays.asList(set.getProduct1Id(),
                        set.getProduct2Id(),
                        set.getProduct3Id())).stream().map(v -> productService.convert2ListDto(productService.selectProduct(
                        v))).toList();
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
