package com.matsinger.barofishserver.domain.data.topbar.repository;

import com.matsinger.barofishserver.domain.data.topbar.domain.TopBar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopBarRepository extends JpaRepository<TopBar, Integer> {

}
