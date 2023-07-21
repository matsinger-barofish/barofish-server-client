package com.matsinger.barofishserver.userauth.repository;

import com.matsinger.barofishserver.userauth.domain.LoginType;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userauth.domain.UserAuthId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, UserAuthId> {

    Optional<UserAuth> findByLoginTypeAndLoginId(LoginType loginType, String userId);

    Optional<UserAuth> findByLoginId(String loginId);

    UserAuth findFirstByUserId(Integer userId);
    void deleteAllByUserId(Integer userId);
}
