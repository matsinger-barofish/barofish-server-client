package com.matsinger.barofishserver.order.orderprductinfo.application;

import com.matsinger.barofishserver.order.dto.OrderProductReq;
import com.matsinger.barofishserver.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.order.orderprductinfo.repository.OrderProductInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderProductInfoQueryService {

    private final OrderProductInfoRepository orderProductInfoRepository;

    public OrderProductInfo findById(int id) {
        return orderProductInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문 상품 정보를 찾을 수 없습니다."));
    }
}
