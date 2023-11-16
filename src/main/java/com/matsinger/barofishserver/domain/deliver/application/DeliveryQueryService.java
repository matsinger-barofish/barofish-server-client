package com.matsinger.barofishserver.domain.deliver.application;

import com.matsinger.barofishserver.domain.deliver.domain.DeliveryCompany;
import com.matsinger.barofishserver.domain.deliver.repository.DeliveryCompanyRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryQueryService {

    private final DeliveryCompanyRepository deliveryCompanyRepository;

    public DeliveryCompany findById(String deliveryCompanyCode) {
        return deliveryCompanyRepository.findById(deliveryCompanyCode)
                                        .orElseThrow(() -> new BusinessException("배송 정보를 찾을 수 없습니다."));
    }
}
