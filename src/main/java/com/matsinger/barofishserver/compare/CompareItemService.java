package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.compare.obejct.*;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.ProductService;
import com.matsinger.barofishserver.product.object.ProductListDto;
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
    private final ProductService productService;


    public List<CompareObject.CompareSetDto> selectPopularCompareSetList(Integer userId) {
        List<Tuple> data = compareSetRepository.selectPopularCompareSetIdList();
        List<CompareObject.CompareSetDto> result = new ArrayList<>();
        for (Tuple t : data) {
            Integer setId = Integer.parseInt(t.get("setId").toString());
            List<Product> products = selectCompareItems(setId);
            List<ProductListDto> productDtos = new ArrayList<>();
            for (Product p : products) {
                ProductListDto dto = p.convert2ListDto();
                dto.setIsLike(checkSaveProduct(userId, p.getId()));
                productDtos.add(dto);

            }
            result.add(CompareObject.CompareSetDto.builder().products(productDtos).build());
        }
        return result;
    }

    public List<CompareObject.RecommendCompareProduct> selectRecommendCompareSetList(Integer userId) {
        List<Tuple> productIdsData = compareSetRepository.selectMostComparedProudct();
        List<CompareObject.RecommendCompareProduct> result = new ArrayList<>();
        for (Tuple t : productIdsData) {
            Integer productId = Integer.parseInt(t.get("productId").toString());
            List<Tuple> recommendProductIdsData = compareSetRepository.selectRecommendCompareSet(productId);
            ProductListDto mainProduct = productService.selectProduct(productId).convert2ListDto();
            mainProduct.setIsLike(checkSaveProduct(userId, productId));
            List<ProductListDto> recommendProductList = new ArrayList<>();
            for (Tuple t2 : recommendProductIdsData) {
                Integer pId = Integer.parseInt(t2.get("pId").toString());
                ProductListDto dto = productService.selectProduct(pId).convert2ListDto();
                dto.setIsLike(checkSaveProduct(userId, pId));
                recommendProductList.add(dto);
            }
            result.add(CompareObject.RecommendCompareProduct.builder().mainProduct(mainProduct).recommendProducts(
                    recommendProductList).build());
        }
        return result;
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
}
