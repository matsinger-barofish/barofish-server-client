package com.matsinger.barofishserver.domain.data.topbar.application;

import com.matsinger.barofishserver.domain.data.topbar.domain.TopBar;
import com.matsinger.barofishserver.domain.data.topbar.domain.TopBarProductMap;
import com.matsinger.barofishserver.domain.data.topbar.repository.TopBarProductMapRepository;
import com.matsinger.barofishserver.domain.data.topbar.repository.TopBarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class TopBarCommandService {
    private final TopBarRepository topBarRepository;

    private final TopBarProductMapRepository topBarProductMapRepository;
    public TopBar add(TopBar topBar) {
        return topBarRepository.save(topBar);
    }

    public TopBarProductMap addProduct(TopBarProductMap data) {
        return topBarProductMapRepository.save(data);
    }
    public TopBar update(Integer id, TopBar data) {
        TopBar topBar = topBarRepository.findById(id).orElseThrow(() -> {
            throw new Error("탑바 카테고리 정보를 찾을 수 없습니다.");
        });
        if (data.getName() != null) {
            topBar.setName(data.getName());
        }
        topBarRepository.save(topBar);
        return topBar;
    }

    public Boolean delete(Integer id) {
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
}
