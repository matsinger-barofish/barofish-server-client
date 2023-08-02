package com.matsinger.barofishserver.compare.application;

import com.matsinger.barofishserver.compare.domain.CompareItem;
import com.matsinger.barofishserver.compare.domain.CompareSet;
import com.matsinger.barofishserver.compare.domain.SaveProduct;
import com.matsinger.barofishserver.compare.domain.SaveProductId;
import com.matsinger.barofishserver.compare.dto.CompareSetDto;
import com.matsinger.barofishserver.compare.dto.RecommendCompareProduct;
import com.matsinger.barofishserver.compare.recommend.application.RecommendCompareSetService;
import com.matsinger.barofishserver.compare.recommend.domain.RecommendCompareSet;
import com.matsinger.barofishserver.compare.recommend.domain.RecommendCompareSetType;
import com.matsinger.barofishserver.compare.recommend.dto.RecommendCompareSetDto;
import com.matsinger.barofishserver.compare.repository.CompareItemRepository;
import com.matsinger.barofishserver.compare.repository.CompareSetRepository;
import com.matsinger.barofishserver.compare.repository.SaveProductRepository;
import com.matsinger.barofishserver.product.application.ProductService;
import com.matsinger.barofishserver.product.domain.Product;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class CompareItemQueryService {
    private final CompareItemRepository compareItemRepository;
    private final CompareSetRepository compareSetRepository;
    private final SaveProductRepository saveProductRepository;
    private final ProductService productService;
    private final RecommendCompareSetService recommendCompareSetService;

    public List<CompareSetDto> selectPopularCompareSetList(Integer userId) {
        List<RecommendCompareSet>
                recommendCompareSets =
                recommendCompareSetService.selectRecommendCompareSetList(RecommendCompareSetType.POPULAR);
        return recommendCompareSets.stream().map(v -> {
            Optional<CompareSet>
                    compareSet =
                    compareSetRepository.selectHavingSet(userId,
                            List.of(v.getProduct1Id(), v.getProduct2Id(), v.getProduct3Id()));
            RecommendCompareSetDto dto = recommendCompareSetService.convert2Dto(v, userId);
            return CompareSetDto.builder().compareSetId(compareSet.isPresent() ? compareSet.get().getId() : null).products(
                    dto.getProducts()).build();
        }).toList();
    }

    public List<RecommendCompareProduct> selectRecommendCompareSetList(Integer userId) {

        List<RecommendCompareSet> recommendCompareSets = recommendCompareSetService.selectRecommendCompareSetByRandom();
        return recommendCompareSets.stream().map(v -> {
            RecommendCompareSetDto dto = recommendCompareSetService.convert2Dto(v, userId);
            return RecommendCompareProduct.builder().recommendProducts(dto.getProducts()).mainProduct(dto.getProducts().get(
                    0)).build();
        }).toList();
    }

    public Boolean checkExistProductSet(Integer userId, List<Integer> productIds) {
        Optional<CompareSet> compareSet = compareSetRepository.selectHavingSet(userId, productIds);
        return compareSet.isPresent();
    }

    public List<CompareSet> selectCompareSetList(Integer userId) {
        return compareSetRepository.findAllByUserId(userId);
    }

    public CompareSet selectCompareSet(Integer id) {
        return compareSetRepository.findById(id).orElseThrow(() -> {
            throw new Error("비교하기 조합 정보를 찾을 수 없습니다.");
        });
    }

    public List<Product> selectCompareItems(Integer setId) {
        List<CompareItem> compareItems = compareItemRepository.findAllByCompareSetId(setId);
        List<Product> products = new ArrayList<>();
        for (CompareItem compareItem : compareItems) {
            Product product = productService.findById(compareItem.getProductId());
            products.add(product);
        }
        return products;
    }

    public List<Product> selectSaveProducts(Integer userId) {
        List<SaveProduct> saveProducts = saveProductRepository.findAllByUserId(userId);
        List<Product> products = new ArrayList<>();
        for (SaveProduct saveProduct : saveProducts) {
            Product product = productService.findById(saveProduct.getProductId());
            products.add(product);
        }
        return products;
    }

    public SaveProduct selectSaveProduct(Integer userId, Integer productId) {
        return saveProductRepository.findById(new SaveProductId(userId, productId)).orElseThrow(() -> {
            throw new Error("저장된 상품 정보를 찾을 수 없습니다.");
        });
    }
}
