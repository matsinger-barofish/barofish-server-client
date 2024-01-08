package com.matsinger.barofishserver.domain.user.exception;

import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.user.domain.UserState;
import com.matsinger.barofishserver.domain.userauth.domain.LoginType;
import com.matsinger.barofishserver.global.exception.BusinessException;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserException {

    private final CustomResponse customResponse;

    public void validateUserState(User findUser) {
        if (findUser.getState().equals(UserState.BANNED)) {
            throw new BusinessException("정지된 유저입니다.");
        }
        if (findUser.getState().equals(UserState.DELETED)) {
            throw new BusinessException("삭제된 유저입니다.");
        }
        if (!findUser.getState().equals(UserState.ACTIVE)) {
            throw new BusinessException("활성화된 유저가 아닙니다.");
        }
    }

    public void nullCheck(LoginType loginType) {
        if (loginType == null) {
            throw new BusinessException("로그인 타입을 입력해주세요.");
        }
    }
    public void nullCheck(String loginId) {
        if (loginId == null) {
            throw new BusinessException("로그인 아이디를 입력해주세요.");
        }
    }

    public void checkUserState(User user) {
        if (user.getState().equals(UserState.BANNED)) {
            throw new BusinessException("정지된 사용자입니다.");
        }
        if (user.getState().equals(UserState.DELETED)) {
            throw new BusinessException("삭제된 사용자입니다.");
        }
    }
}
