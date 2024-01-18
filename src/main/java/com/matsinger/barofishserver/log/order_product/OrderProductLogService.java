package com.matsinger.barofishserver.log.order_product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProductLogService {

    private final OrderProductLogRepository productLogRepository;

    public void saveOrderProductLog(OrderProductLog orderProductLog) {
        productLogRepository.save(orderProductLog);
    }
}
