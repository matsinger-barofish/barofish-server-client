package com.matsinger.barofishserver.data.topbar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopBarRepository extends JpaRepository<TopBar, Integer> {

}
