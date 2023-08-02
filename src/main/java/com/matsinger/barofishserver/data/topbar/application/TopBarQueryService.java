package com.matsinger.barofishserver.data.topbar.application;

import com.matsinger.barofishserver.data.topbar.domain.TopBar;
import com.matsinger.barofishserver.data.topbar.domain.TopBarProductMap;
import com.matsinger.barofishserver.data.topbar.repository.TopBarProductMapRepository;
import com.matsinger.barofishserver.data.topbar.repository.TopBarRepository;
import com.matsinger.barofishserver.product.domain.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class TopBarQueryService {
    private final TopBarRepository topBarRepository;

    private final TopBarProductMapRepository topBarProductMapRepository;

    public List<TopBar> selectTopBarList() {
        return topBarRepository.findAll();
    }

    public TopBar selectTopBar(Integer id) {
        return topBarRepository.findById(id).orElseThrow(() -> {
            throw new Error("탑바 카테고리 정보를 찾을 수 없습니다.");
        });
    }

    public List<Product> selectTopBarProducts(Integer topBarId) {
        List<TopBarProductMap> curationProductMapList = topBarProductMapRepository.findAllByTopBar_Id(topBarId);
        List<Product> products = new ArrayList<>();
        for (TopBarProductMap item : curationProductMapList) {
            products.add(item.getProduct());
        }
        return products;
    }
}
