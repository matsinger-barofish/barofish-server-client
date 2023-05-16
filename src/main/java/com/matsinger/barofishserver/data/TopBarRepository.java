package com.matsinger.barofishserver.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopBarRepository extends JpaRepository<TopBar, Integer> {

}
