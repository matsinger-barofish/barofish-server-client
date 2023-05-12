package com.matsinger.barofishserver.data;

import com.matsinger.barofishserver.product.Product;
import com.matsinger.barofishserver.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CurationService {

    @Autowired
    private CurationRepository curationRepository;
    @Autowired
    private CurationProductMapRepository curationProductRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<Curation> selectCurations() {
        return curationRepository.findAll(Sort.by(Sort.Direction.ASC, "sortNo"));
    }

    public List<Product> selectCurationProducts(Long curationId) {
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

    public Optional<Curation> findById(Long id){
        return curationRepository.findById(id);
    }

    public Boolean delete(Long id) {
        try {
            curationRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<CurationProductMap> addProduct(Long curationId, List<Long> productIds){
        ArrayList<CurationProductMap> curationProductMapArrayList = new ArrayList<CurationProductMap>();
        for(Long id : productIds){
            CurationProductMap data = new CurationProductMap();
            Curation curation = curationRepository.findById(curationId).orElseThrow();
            Product product = productRepository.findById(id).orElseThrow();

            data.setCuration(curation);
            data.setProduct(product);
            curationProductMapArrayList.add(data);
        }
        return curationProductRepository.saveAll(curationProductMapArrayList);
    }
}
