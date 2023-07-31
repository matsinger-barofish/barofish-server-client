package com.matsinger.barofishserver.user.application;

import com.matsinger.barofishserver.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.user.repository.UserRepository;
import com.matsinger.barofishserver.user.dto.UserLoginReq;
import com.matsinger.barofishserver.user.domain.User;
import com.matsinger.barofishserver.user.exception.UserException;
import com.matsinger.barofishserver.userauth.domain.UserAuth;
import com.matsinger.barofishserver.userauth.application.UserAuthQueryService;
import com.matsinger.barofishserver.userauth.repository.UserAuthRepository;
import com.matsinger.barofishserver.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.userinfo.dto.UserInfoDto;
import com.matsinger.barofishserver.userinfo.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserInfoRepository userInfoRepository;
    private final UserAuthRepository userAuthRepository;
    private final DeliverPlaceRepository deliverPlaceRepository;

    private final UserAuthQueryService userAuthQueryService;

    private final UserException userValidator;

    public User login(UserLoginReq request) {
        UserAuth userAuth = userAuthQueryService.login(request);
        User findUser = userRepository.findById(userAuth.getUserId())
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("유저 정보를 찾을 수 없습니다.");
                });
        userValidator.validateUserState(findUser);
        return findUser;
    }

    public Page<UserInfoDto> findPagedAndSpecificatedUserInfos(Specification<UserInfo> spec, PageRequest pageRequest) {
        return userInfoRepository.findAll(spec, pageRequest).map(userInfo -> {

            UserInfoDto userInfoDto = userInfo.convert2Dto();
            int userId = userInfo.getUserId();

            User findUser = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("유저 정보를 찾을 수 없습니다."));
            userInfoDto.setUser(findUser.convert2Dto());

            UserAuth findUserAuth = userAuthRepository.findFirstByUserId(userId);
            if(findUserAuth!=null) {
                findUserAuth.setPassword(null);
                userInfoDto.setAuth(findUserAuth.convert2Dto());
            }

            List<DeliverPlace> deliverPlaces = deliverPlaceRepository.findAllByUserId(userId);
            userInfoDto.setDeliverPlaces(deliverPlaces);

            return userInfoDto;
        });
    }

    public UserInfoDto manageByUserId(Integer userId) {
        UserInfo findUserInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
                });
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("유저를 찾을 수 없습니다.");
                });
        List<DeliverPlace> deliverPlaces = deliverPlaceRepository.findAllByUserId(userId);

        UserAuth findUserAuth = userAuthRepository.findFirstByUserId(userId);
        findUserAuth.setPassword(null);

        UserInfoDto userInfoDto = findUserInfo.convert2Dto();
        userInfoDto.setUser(findUser.convert2Dto());
        userInfoDto.setAuth(findUserAuth.convert2Dto());
        userInfoDto.setDeliverPlaces(deliverPlaces);
        return userInfoDto;
    }
}