package com.matsinger.barofishserver.userauth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, UserAuthId> {

    Optional<UserAuth> findByLoginTypeAndLoginId(LoginType loginType, String userId);

    Optional<UserAuth> findByLoginId(String loginId);

    UserAuth findFirstByUserId(Integer userId);
}
