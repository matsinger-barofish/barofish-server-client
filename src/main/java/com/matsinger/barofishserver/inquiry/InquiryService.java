package com.matsinger.barofishserver.inquiry;

import com.matsinger.barofishserver.product.ProductRepository;
import com.matsinger.barofishserver.product.object.Product;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserService userService;
    private final StoreService storeService;
    @Autowired
    private final ProductRepository productRepository;

    public InquiryDto convert2Dto(Inquiry inquiry, Integer productId, Integer userId) {
        InquiryDto inquiryDto = inquiry.convert2Dto();
        Product product = productRepository.findById(productId).orElseThrow(() -> {
            throw new Error("상품 정보를 찾을 수 없습니다.");
        });
        inquiryDto.setProduct(product.convert2ListDto());
        inquiryDto.setStore(storeService.selectStoreInfo(product.getStoreId()).convert2Dto());
        inquiryDto.setUser(userService.selectUserInfo(userId).convert2Dto());
        return inquiryDto;
    }

    public Page<Inquiry> selectAllInquiryList(PageRequest pageRequest, Specification<Inquiry> spec) {
        return inquiryRepository.findAll(spec, pageRequest);
    }

    public Page<Inquiry> selectStoreInquiryList(Integer storeId, PageRequest pageRequest) {
        return inquiryRepository.findAllByProduct_StoreId(storeId,pageRequest);
    }

    public List<Inquiry> selectInquiryListWithProductId(Integer productId) {
        return inquiryRepository.findAllByProductId(productId);
    }

    public Inquiry selectInquiry(Integer inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(() -> {
            throw new Error("문의 내용을 찾을 수 없습니다.");
        });
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

    public void deleteInquiryByUserId(Integer userId){
        inquiryRepository.deleteAllByUserId(userId);
    }
}
