package com.matsinger.barofishserver.data;

import com.matsinger.barofishserver.product.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class TopBarService {
    private final TopBarRepository topBarRepository;

    private final TopBarProductMapRepository topBarProductMapRepository;

    TopBar add(TopBar topBar) {
        return topBarRepository.save(topBar);
    }

    TopBarProductMap addProduct(TopBarProductMap data) {
        return topBarProductMapRepository.save(data);
    }

    public List<TopBar> selectTopBarList() {
        return topBarRepository.findAll();
    }

    TopBar selectTopBar(Integer id) {
        return topBarRepository.findById(id).orElseThrow(() -> {
            throw new Error("탑바 카테고리 정보를 찾을 수 없습니다.");
        });
    }

    TopBar update(Integer id, TopBar data) {
        TopBar topBar = topBarRepository.findById(id).orElseThrow(() -> {
            throw new Error("탑바 카테고리 정보를 찾을 수 없습니다.");
        });
        if (data.getName() != null) {
            topBar.setName(data.getName());
        }
        topBarRepository.save(topBar);
        return topBar;
    }

    Boolean delete(Integer id) {
        try {
            topBarRepository.findById(id).orElseThrow(() -> {
                throw new Error("탑바 카테고리 정보를 찾을 수 었습니다.");
            });
            topBarProductMapRepository.deleteAllByTopBar_Id(id);
            topBarRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
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
