package com.matsinger.barofishserver.data.curation.application;

import com.matsinger.barofishserver.data.curation.domain.Curation;
import com.matsinger.barofishserver.data.curation.domain.CurationProductMap;
import com.matsinger.barofishserver.data.curation.repository.CurationProductMapRepository;
import com.matsinger.barofishserver.data.curation.repository.CurationRepository;
import com.matsinger.barofishserver.product.domain.Product;
import com.matsinger.barofishserver.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CurationQueryService {
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
    public Curation selectCuration(Integer id) {
        return curationRepository.findById(id).orElseThrow(() -> {
            throw new Error("큐레이션 정보를 찾을 수 없습니다.");
        });
    }
    public Integer selectMaxSortNo() {
        return Integer.valueOf(curationRepository.selectMaxSortNo().get("sortNo").toString());
    }

}
