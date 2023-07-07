package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.user.object.UserInfo;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer>, JpaSpecificationExecutor<UserInfo> {
    //    Page<UserInfo> findAll(Sort sort, Pageable pageable);
    UserInfo findByPhone(String phone);

    Boolean existsByPhone(String phone);

    Boolean existsByNickname(String nickname);


    Page<UserInfo> findAll(Specification<UserInfo> spec, Pageable pageable);
}