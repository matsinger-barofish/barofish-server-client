package com.matsinger.barofishserver.compare;

import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        compareSet.setProducts(products);
        return compareSet;
    }

    public void deleteCompareSet(Integer id) {
        CompareSet compareSet = compareSetRepository.findById(id).orElseThrow(() -> {
            throw new Error("이미 제거된 비교하기 조합입니다.");
        });
        compareItemRepository.deleteByCompareSetId(compareSet.getId());
    }

    public Boolean addSaveProduct(Integer userId, Integer productId) {
        SaveProduct saveProduct = SaveProduct.builder().userId(userId).productId(productId).build();
        saveProductRepository.save(saveProduct);
        return true;
    }

    public void deleteSaveProduct(List<SaveProduct> saveProducts) {
        saveProductRepository.deleteAll(saveProducts);
    }
}
