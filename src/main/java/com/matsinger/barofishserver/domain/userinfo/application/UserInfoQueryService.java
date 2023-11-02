package com.matsinger.barofishserver.domain.userinfo.application;

import com.matsinger.barofishserver.domain.compare.application.CompareItemQueryService;
import com.matsinger.barofishserver.domain.notification.repository.NotificationRepository;
import com.matsinger.barofishserver.domain.review.repository.ReviewRepository;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.domain.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.domain.userinfo.dto.UserInfoDto;
import com.matsinger.barofishserver.domain.userinfo.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserInfoQueryService {

    private final UserInfoRepository userInfoRepository;
    private final DeliverPlaceRepository deliverPlaceRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationRepository notificationRepository;
    private final CompareItemQueryService compareItemQueryService;

    public UserInfoDto showMyPage(Integer userId) {

        UserInfo findUserInfo = userInfoRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("유저 정보를 찾을 수 없습니다.");
                });

        List<DeliverPlace> deliverPlaces = deliverPlaceRepository.findAllByUserId(userId);
        Integer reviewCount = reviewRepository.countAllByUserId(userId);
        Integer notificationCount = notificationRepository.countAllByUserId(userId);
        Integer saveProductCount = compareItemQueryService.countSaveProductWithUserId(userId);
        return UserInfoDto.builder()
                .userId(userId)
                .profileImage(findUserInfo.getProfileImage())
                .grade(findUserInfo.getGrade())
                .email(findUserInfo.getEmail())
                .name(findUserInfo.getName())
                .nickname(findUserInfo.getNickname())
                .phone(findUserInfo.getPhone())
                .isAgreeMarketing(findUserInfo.getIsAgreeMarketing())
                .reviewCount(reviewCount)
                .notificationCount(notificationCount)
                .deliverPlaces(deliverPlaces)
                .point(findUserInfo.getPoint())
                .saveProductCount(saveProductCount)
                .build();
    }

    public Integer getAppleUserId(String phone) {

        Optional<UserInfo> userInfoOptional = userInfoRepository.findByPhone(phone);
        if (userInfoOptional.isPresent()) {
            return userInfoOptional.get().getUserId();
        }

//        for (UserAuth userAuth : userInfoOptional.get().getUser().getUserAuth()) {
//            if (userAuth.getLoginType() == LoginType.APPLE) {
//                return userAuth.getUserId();
//            }
//        }

        return null;
    }
}