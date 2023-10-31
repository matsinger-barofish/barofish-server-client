package com.matsinger.barofishserver.domain.userauth.application;

import com.matsinger.barofishserver.domain.user.dto.SnsJoinReq;
import com.matsinger.barofishserver.domain.user.dto.UserLoginReq;
import com.matsinger.barofishserver.domain.userauth.domain.LoginType;
import com.matsinger.barofishserver.domain.userauth.domain.UserAuth;
import com.matsinger.barofishserver.domain.userauth.repository.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAuthQueryService {

    private final UserAuthRepository userAuthRepository;

    public UserAuth login(UserLoginReq request) {
        UserAuth
                findUserAuth =
                userAuthRepository.findByLoginTypeAndLoginId(request.getLoginType(),
                        request.getLoginId()).orElseThrow(() -> {
                    throw new IllegalStateException("아이디 및 비밀번호를 확인해주세요.");
                });
        if (findUserAuth.getLoginType() != LoginType.IDPW) {
            throw new IllegalArgumentException("아이디, 패스워스를 사용해 로그인 해주세요.");
        }
        if (!BCrypt.checkpw(request.getPassword(), findUserAuth.getPassword())) {
            throw new IllegalArgumentException("아이디 및 비밀번호를 확인해주세요.");
        }
        return findUserAuth;
    }

    public boolean checkUserExist(SnsJoinReq request) {
        return userAuthRepository.existsByLoginIdAndLoginType(request.getLoginId(), request.getLoginType());
    }
}
