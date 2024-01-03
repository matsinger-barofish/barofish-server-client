package com.matsinger.barofishserver.domain.order.orderprductinfo.application;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.order.orderprductinfo.repository.OrderProductInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProductInfoCommandService {

    private final OrderProductInfoRepository orderProductInfoRepository;

    public void saveAll(List<OrderProductInfo> orderProductInfos) {
        orderProductInfoRepository.saveAll(orderProductInfos);
    }
}
