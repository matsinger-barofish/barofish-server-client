package com.matsinger.barofishserver.data.topbar.repository;

import com.matsinger.barofishserver.data.topbar.domain.TopBarProductMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopBarProductMapRepository extends JpaRepository<TopBarProductMap, Integer> {
    List<TopBarProductMap> findAllByTopBar_Id(Integer id);

    void deleteAllByTopBar_Id(Integer id);
}
