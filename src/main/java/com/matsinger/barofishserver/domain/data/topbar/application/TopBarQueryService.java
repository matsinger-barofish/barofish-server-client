package com.matsinger.barofishserver.domain.data.topbar.application;

import com.matsinger.barofishserver.domain.data.topbar.domain.TopBar;
import com.matsinger.barofishserver.domain.data.topbar.domain.TopBarProductMap;
import com.matsinger.barofishserver.domain.data.topbar.repository.TopBarProductMapRepository;
import com.matsinger.barofishserver.domain.data.topbar.repository.TopBarRepository;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.global.exception.BusinessException;
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
            throw new BusinessException("탑바 카테고리 정보를 찾을 수 없습니다.");
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
