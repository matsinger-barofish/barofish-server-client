package com.matsinger.barofishserver.userinfo.repository;

import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer>, JpaSpecificationExecutor<UserInfo> {
    //    Page<UserInfo> findAll(Sort sort, Pageable pageable);
    Optional<UserInfo> findByPhone(String phone);

    Optional<UserInfo> findByNickname(String nickname);

    Boolean existsByPhone(String phone);

    Boolean existsByNickname(String nickname);

    Boolean existsByUserId(Integer userId);
    Page<UserInfo> findAll(Specification<UserInfo> spec, Pageable pageable);

    Optional<UserInfo> findByUserId(int userId);
}