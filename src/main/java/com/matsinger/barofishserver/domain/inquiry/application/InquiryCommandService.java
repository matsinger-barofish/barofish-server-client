package com.matsinger.barofishserver.domain.inquiry.application;

import com.matsinger.barofishserver.domain.inquiry.domain.Inquiry;
import com.matsinger.barofishserver.domain.inquiry.dto.InquiryDto;
import com.matsinger.barofishserver.domain.inquiry.repository.InquiryRepository;
import com.matsinger.barofishserver.domain.product.domain.Product;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.userinfo.domain.UserInfo;
import com.matsinger.barofishserver.domain.userinfo.repository.UserInfoRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class InquiryCommandService {
    private final StoreService storeService;
    private final InquiryRepository inquiryRepository;
    private final ProductRepository productRepository;
    private final UserInfoRepository userInfoRepository;

    public InquiryDto convert2Dto(Inquiry inquiry, Integer userId) {
        InquiryDto inquiryDto = inquiry.convert2Dto();
        Product product = productRepository.findById(inquiry.getProductId()).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
        });
        inquiryDto.setProduct(product.convert2ListDto());
        inquiryDto.setStore(storeService.selectStoreInfo(product.getStoreId()).convert2Dto());
        UserInfo
                findUserInfo =
                userInfoRepository.findById(inquiry.getUserId()).orElseThrow(() -> new BusinessException(
                        "유저 정보를 찾을 수 " + "없습니다."));
        inquiryDto.setUser(findUserInfo.convert2Dto());
        inquiryDto.setIsMine(userId != null && userId == inquiry.getUserId());
        return inquiryDto;
    }

    public Inquiry addInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    public Inquiry updateInquiry(Inquiry inquiry) {
        return inquiryRepository.save(inquiry);
    }

    public Boolean deleteInquiry(Integer inquiryId) {
        try {
            inquiryRepository.deleteById(inquiryId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteInquiryByUserId(Integer userId) {
        inquiryRepository.deleteAllByUserId(userId);
    }

    @Transactional
    public void deleteInquiryWithIds(List<Integer> ids) {
        inquiryRepository.deleteAllByIdIn(ids);
    }
}
