package com.matsinger.barofishserver.domain.data.curation.application;

import com.matsinger.barofishserver.domain.data.curation.domain.Curation;
import com.matsinger.barofishserver.domain.data.curation.domain.CurationProductMap;
import com.matsinger.barofishserver.domain.data.curation.repository.CurationProductMapRepository;
import com.matsinger.barofishserver.domain.data.curation.repository.CurationRepository;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CurationCommandService {
    private final CurationRepository curationRepository;
    private final CurationProductMapRepository curationProductRepository;

    private final ProductRepository productRepository;
    public Curation add(Curation curation) {
        return curationRepository.save(curation);
    }

    public Curation update(Curation curation) {
        return curationRepository.save(curation);
    }
    @Transactional
    public void deleteWithProductId(Integer productId) {
        curationProductRepository.deleteAllByProductId(productId);
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
    public boolean checkExistCurationProductMap(Integer curationId, Integer productId) {
        return curationProductRepository.existsByCurationIdAndProductId(curationId, productId);
    }
    public void deleteProducts(Integer curationId, List<Integer> productIds) {
        curationProductRepository.deleteAllByProductIdIn(curationId, productIds);
    }

    public void updateAllCuration(List<Curation> curations) {
        curationRepository.saveAll(curations);
    }
}
