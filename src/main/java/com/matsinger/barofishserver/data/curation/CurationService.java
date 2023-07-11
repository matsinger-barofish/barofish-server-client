package com.matsinger.barofishserver.data.curation;

import com.matsinger.barofishserver.data.curation.object.Curation;
import com.matsinger.barofishserver.data.curation.object.CurationProductMap;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CurationService {

    private final CurationRepository curationRepository;
    private final CurationProductMapRepository curationProductRepository;

    private final ProductRepository productRepository;

    public Page<Curation> selectCurationListByAdmin(PageRequest pageRequest) {
        return curationRepository.findAll(pageRequest);
    }

    public List<Curation> selectCurations() {
        return curationRepository.findAll(Sort.by(Sort.Direction.ASC, "sortNo"));
    }

    public List<Product> selectCurationProducts(Integer curationId) {
        List<CurationProductMap> curationProductMapList = curationProductRepository.findAllByCuration_Id(curationId);
        List<Product> products = new ArrayList<>();
        for (CurationProductMap item : curationProductMapList) {
            products.add(item.getProduct());
        }
        return products;
    }

    public List<Product> selectCurationProducts(Integer curationId, PageRequest pageRequest) {
        List<CurationProductMap>
                curationProductMapList =
                curationProductRepository.findAllByCuration_Id(curationId, pageRequest);
        List<Product> products = new ArrayList<>();
        for (CurationProductMap item : curationProductMapList) {
            products.add(item.getProduct());
        }
        return products;
    }

    public Curation add(Curation curation) {
        return curationRepository.save(curation);
    }

    public Curation update(Curation curation) {
        return curationRepository.save(curation);
    }

    public Curation selectCuration(Integer id) {
        return curationRepository.findById(id).orElseThrow(() -> {
            throw new Error("큐레이션 정보를 찾을 수 없습니다.");
        });
    }

    @Transactional
    public void deleteWithProductId(Integer productId) {
        curationProductRepository.deleteAllByProductId(productId);
    }

    public Integer selectMaxSortNo() {
        return Integer.valueOf(curationRepository.selectMaxSortNo().get("sortNo").toString());
    }

    public Boolean delete(Integer id) {
        try {
            curationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<CurationProductMap> addProduct(Integer curationId, List<Integer> productIds) {
        ArrayList<CurationProductMap> curationProductMapArrayList = new ArrayList<>();
        for (Integer id : productIds) {
            if (checkExistCurationProductMap(curationId, id)) continue;
            CurationProductMap data = new CurationProductMap();
            Curation curation = curationRepository.findById(curationId).orElseThrow();
            Product product = productRepository.findById(id).orElseThrow();

            data.setCuration(curation);
            data.setProduct(product);
            curationProductMapArrayList.add(data);
        }
        return curationProductRepository.saveAll(curationProductMapArrayList);
    }

    public void deleteProducts(Integer curationId, List<Integer> productIds) {
        curationProductRepository.deleteAllByProductIdIn(curationId, productIds);
    }

    public void updateAllCuration(List<Curation> curations) {
        curationRepository.saveAll(curations);
    }

    public boolean checkExistCurationProductMap(Integer curationId, Integer productId) {
        return curationProductRepository.existsByCurationIdAndProductId(curationId, productId);
    }
}
