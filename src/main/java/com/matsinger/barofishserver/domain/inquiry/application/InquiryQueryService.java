package com.matsinger.barofishserver.domain.inquiry.application;

import com.matsinger.barofishserver.domain.inquiry.domain.Inquiry;
import com.matsinger.barofishserver.domain.inquiry.repository.InquiryRepository;
import com.matsinger.barofishserver.domain.product.repository.ProductRepository;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.userinfo.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class InquiryQueryService {
    private final StoreService storeService;
    private final InquiryRepository inquiryRepository;
    private final ProductRepository productRepository;
    private final UserInfoRepository userInfoRepository;

    public Page<Inquiry> selectAllInquiryList(PageRequest pageRequest,
                                              Specification<Inquiry> spec) {
        return inquiryRepository.findAll(spec, pageRequest);
    }

    public org.springframework.data.domain.Page<Inquiry> selectStoreInquiryList(Integer storeId,
                                                                                PageRequest pageRequest) {
        return inquiryRepository.findAllByProduct_StoreId(storeId, pageRequest);
    }

    public List<Inquiry> selectInquiryListWithProductId(Integer productId) {
        return inquiryRepository.findAllByProductId(productId);
    }

    public Inquiry selectInquiry(Integer inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(() -> {
            throw new Error("문의 내용을 찾을 수 없습니다.");
        });
    }

    public List<Inquiry> selectInquiryListWithUserId(Integer userId) {
        return inquiryRepository.findAllByUserId(userId);
    }
}
