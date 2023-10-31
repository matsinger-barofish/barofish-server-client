package com.matsinger.barofishserver.domain.userauth.repository;

import com.matsinger.barofishserver.domain.userauth.domain.LoginType;
import com.matsinger.barofishserver.domain.userauth.domain.UserAuth;
import com.matsinger.barofishserver.domain.userauth.domain.UserAuthId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, UserAuthId> {

    Optional<UserAuth> findByLoginTypeAndLoginId(LoginType loginType, String userId);

    Optional<UserAuth> findByLoginTypeAndUserId(LoginType loginType, Integer id);

    Optional<UserAuth> findByLoginId(String loginId);

    Optional<UserAuth> findByLoginIdAndLoginType(String loginId, LoginType loginType);

    UserAuth findFirstByUserId(Integer userId);

    void deleteAllByUserId(Integer userId);

    void deleteAllByUserIdIn(List<Integer> userIds);

    boolean existsByLoginIdAndLoginType(String loginId, LoginType loginType);
}
