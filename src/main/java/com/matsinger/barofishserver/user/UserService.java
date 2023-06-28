package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.grade.Grade;
import com.matsinger.barofishserver.grade.GradeRepository;
import com.matsinger.barofishserver.user.object.*;
import com.matsinger.barofishserver.userauth.LoginType;
import com.matsinger.barofishserver.userauth.UserAuth;
import com.matsinger.barofishserver.userauth.UserAuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final UserInfoRepository userInfoRepository;

    private final UserAuthRepository userAuthRepository;
    private final DeliverPlaceRepository deliverPlaceRepository;
    private final GradeRepository gradeRepository;

    public Page<UserInfoDto> selectUserInfoList(PageRequest pageRequest, Specification<UserInfo> spec) {
        return userInfoRepository.findAll(spec, pageRequest).map(userInfo -> {
            UserInfoDto userInfoDto = userInfo.convert2Dto();
            userInfoDto.setUser(selectUser(userInfo.getUserId()).convert2Dto());
            UserAuth userAuth = selectUserAuth(userInfo.getUserId());
            if (userAuth != null) userAuth.setPassword(null);
            userInfoDto.setAuth(userAuth.convert2Dto());
            List<DeliverPlace> deliverPlaces = selectUserDeliverPlaceList(userInfo.getUserId());
            userInfoDto.setDeliverPlaces(deliverPlaces);
            return userInfoDto;
        });
    }

    public DeliverPlace selectDeliverPlace(Integer id) {
        return deliverPlaceRepository.findById(id).orElseThrow(() -> {
            throw new Error("배송지 정보를 찾을 수 없습니다.");
        });
    }

    public Boolean checkExistWithPhone(String phone) {
        return userInfoRepository.existsByPhone(phone);
    }

    public Boolean checkExistWithNickname(String nickname) {
        return userInfoRepository.existsByNickname(nickname);
    }

    public Optional<User> selectUserOptional(Integer id) {
        try {
            return userRepository.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public UserInfo selectUserInfo(Integer id) {
        return userInfoRepository.findById(id).orElseThrow(() -> {
            throw new Error("유저 정보를 찾을 수 없습니다.");
        });
    }

    public UserAuth selectUserAuth(Integer id) {
        return userAuthRepository.findFirstByUserId(id);
    }

    public List<User> selectUserList() {
        return userRepository.findAll();
    }

    public User selectUser(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> {
            throw new Error("유저 정보를 찾을 수 없습니다.");
        });
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public UserInfo addUser(User user, UserAuth userAuth, UserInfo userInfo, DeliverPlace deliverPlace) {
        User res = userRepository.save(user);
        userAuth.setUserId(res.getId());
        userInfo.setUser(res);
        deliverPlace.setUserId(res.getId());
        userAuthRepository.save(userAuth);
        UserInfo result = userInfoRepository.save(userInfo);
        deliverPlaceRepository.save(deliverPlace);
        return result;
    }

    public UserAuth selectUserByLoginId(LoginType loginType, String loginId) {
        try {
            return userAuthRepository.findByLoginTypeAndLoginId(loginType, loginId).orElseThrow();
        } catch (Exception e) {
            return null;
        }
    }

    public Optional<UserAuth> checkUserAuthExist(LoginType loginType, String loginId) {
        return userAuthRepository.findByLoginTypeAndLoginId(loginType, loginId);
    }

    public List<DeliverPlace> selectUserDeliverPlaceList(Integer userId) {
        return deliverPlaceRepository.findAllByUserId(userId);
    }

    public UserAuth addUserAuth(UserAuth userAuth) {
        return userAuthRepository.save(userAuth);
    }

    public UserInfo addUserInfo(UserInfo userInfo) {
        return userInfoRepository.save(userInfo);
    }

    public List<User> updateUserState(List<Integer> userIds, UserState state) {
        List<User> users = userRepository.findAllById(userIds);
        for (User user : users) {
            user.setState(state);
        }
        users = userRepository.saveAll(users);
        return users;
    }

    public void updateUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }

    public void updateUerPassword(UserAuth userAuth) {
        userAuthRepository.save(userAuth);
    }

    public List<Grade> selectGradeList() {
        return gradeRepository.findAll();
    }

    public Grade selectGrade(Integer id) {
        return gradeRepository.findById(id).orElseThrow(() -> {
            throw new Error("등급 정보를 찾을 수 없습니다.");
        });
    }

    public void addPoint(UserInfo userInfo, Integer point) {
        userInfo.setPoint(userInfo.getPoint() + point);
        userInfoRepository.save(userInfo);
    }

    public List<User> selectUserWithState(UserState state) {
        return userRepository.findAllByState(state);
    }

    public String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int randomType = random.nextInt(3);
            char randomChar;

            switch (randomType) {
                case 0:
                    randomChar = (char) (random.nextInt(10) + '0');  // Numbers
                    break;
                case 1:
                    randomChar = (char) (random.nextInt(26) + 'A');  // Uppercase letters
                    break;
                default:
                    randomChar = (char) (random.nextInt(26) + 'a');  // Lowercase letters
                    break;
            }

            sb.append(randomChar);
        }

        return sb.toString();
    }


    public UserInfo selectUserWithPhone(String phone) {
        return userInfoRepository.findByPhone(phone);
    }
}
