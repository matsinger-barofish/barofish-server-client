package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.user.object.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo,Integer> {
    Page<UserInfo> findAll(Pageable pageable);
}
