package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.category.CategoryFilterRepository;
import com.matsinger.barofishserver.compare.filter.CompareFilterDto;
import com.matsinger.barofishserver.compare.filter.CompareFilterService;
import com.matsinger.barofishserver.compare.obejct.*;
import com.matsinger.barofishserver.product.filter.ProductFilterService;
import com.matsinger.barofishserver.product.filter.ProductFilterValueDto;
import com.matsinger.barofishserver.product.object.OptionItem;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.object.StoreInfo;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class CompareItemService {
    private final CompareItemRepository compareItemRepository;
    private final CompareSetRepository compareSetRepository;

    private final SaveProductRepository saveProductRepository;
    private final StoreService storeService;
    private final ProductService productService;
    private final ProductFilterService productFilterService;
    private final CategoryFilterRepository categoryFilterRepository;
    private final CompareFilterService compareFilterService;
    private final RecommendCompareSetService recommendCompareSetService;


    public List<CompareObject.CompareSetDto> selectPopularCompareSetList(Integer userId) {
        List<RecommendCompareSet>
                recommendCompareSets =
                recommendCompareSetService.selectRecommendCompareSetList(RecommendCompareSetType.POPULAR);
        return recommendCompareSets.stream().map(v -> {
            RecommendCompareSetDto dto = recommendCompareSetService.convert2Dto(v, userId);
            return CompareObject.CompareSetDto.builder().compareSetId(null).products(dto.getProducts()).build();
        }).toList();
    }

    public List<CompareObject.RecommendCompareProduct> selectRecommendCompareSetList(Integer userId) {
        List<RecommendCompareSet> recommendCompareSets = recommendCompareSetService.selectRecommendCompareSetByRandom();
        return recommendCompareSets.stream().map(v -> {
            RecommendCompareSetDto dto = recommendCompareSetService.convert2Dto(v, userId);
            return CompareObject.RecommendCompareProduct.builder().recommendProducts(dto.getProducts()).mainProduct(dto.getProducts().get(
                    0)).build();
        }).toList();
    }

    public Boolean checkExistProductSet(Integer userId, List<Integer> productIds) {
        List<Tuple> data = compareSetRepository.checkExistHavingSet(userId, productIds);
        return data.size() != 0;
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
            Product product = productService.selectProduct(compareItem.getProductId());
            products.add(product);
        }
        return products;
    }

    public List<Product> selectSaveProducts(Integer userId) {
        List<SaveProduct> saveProducts = saveProductRepository.findAllByUserId(userId);
        List<Product> products = new ArrayList<>();
        for (SaveProduct saveProduct : saveProducts) {
            Product product = productService.selectProduct(saveProduct.getProductId());
            products.add(product);
        }
        return products;
    }

    public SaveProduct selectSaveProduct(Integer userId, Integer productId) {
        return saveProductRepository.findById(new SaveProductId(userId, productId)).orElseThrow(() -> {
            throw new Error("저장된 상품 정보를 찾을 수 없습니다.");
        });
    }

    public CompareSet addCompareSet(Integer userId, List<Integer> productIds) {
        CompareSet
                compareSet =
                CompareSet.builder().userId(userId).createdAt(new Timestamp(System.currentTimeMillis())).build();
        compareSetRepository.save(compareSet);
        List<CompareItem> compareItems = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        for (Integer productId : productIds) {
            Product product = productService.selectProduct(productId);
            CompareItem
                    compareItem =
                    CompareItem.builder().compareSetId(compareSet.getId()).productId(product.getId()).build();
            compareItems.add(compareItem);
            compareItemRepository.save(compareItem);
            products.add(product);
        }
//        compareSet.setProducts(products);
        return compareSet;
    }

    @Transactional
    public void deleteCompareSet(Integer id) {
        CompareSet compareSet = compareSetRepository.findById(id).orElseThrow(() -> {
            throw new Error("이미 제거된 비교하기 조합입니다.");
        });
        compareItemRepository.deleteByCompareSetId(compareSet.getId());
        compareSetRepository.deleteById(id);
    }

    public Boolean addSaveProduct(Integer userId, Integer productId) {
        SaveProduct saveProduct = SaveProduct.builder().userId(userId).productId(productId).build();
        saveProductRepository.save(saveProduct);
        return true;
    }

    @Transactional
    public void deleteSaveProduct(List<SaveProduct> saveProducts) {
        saveProductRepository.deleteAll(saveProducts);
    }

    public Boolean checkSaveProduct(Integer userId, Integer productId) {
        return saveProductRepository.existsById(new SaveProductId(userId, productId));
    }

    public CompareProductDto convertProduct2Dto(Product product) {
        StoreInfo storeInfo = storeService.selectStoreInfo(product.getStoreId());
        List<ProductFilterValueDto>
                filterValues =
                productFilterService.selectProductFilterValueListWithProductId(product.getId());
        List<CompareFilterDto>
                compareFilterDtos =
                categoryFilterRepository.findAllByCategoryId(product.getCategory().getCategoryId()).stream().map(v -> compareFilterService.selectCompareFilter(
                        v.getCompareFilterId()).convert2Dto()).toList();
        OptionItem optionItem = productService.selectOptionItem(product.getRepresentOptionItemId());
        return CompareProductDto.builder().id(product.getId()).image(product.getImages().substring(1,
                product.getImages().length() -
                        1).split(",")[0]).title(product.getTitle()).originPrice(optionItem.getOriginPrice()).storeName(
                storeInfo.getName()).discountPrice(optionItem.getDiscountPrice()).deliveryFee(product.getDeliveryFee()).compareFilters(
                compareFilterDtos).filterValues(filterValues).build();
    }
}
