package com.matsinger.barofishserver.data.curation;

import com.matsinger.barofishserver.data.curation.object.Curation;
import com.matsinger.barofishserver.data.curation.object.CurationProductMap;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CurationService {

    @Autowired
    private final CurationRepository curationRepository;
    @Autowired
    private final CurationProductMapRepository curationProductRepository;

    @Autowired
    private final ProductRepository productRepository;

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
        ArrayList<CurationProductMap> curationProductMapArrayList = new ArrayList<CurationProductMap>();
        for (Integer id : productIds) {
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
}
