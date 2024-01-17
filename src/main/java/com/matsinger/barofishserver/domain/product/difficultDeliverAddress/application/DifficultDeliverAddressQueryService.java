package com.matsinger.barofishserver.domain.product.difficultDeliverAddress.application;

import com.matsinger.barofishserver.domain.order.domain.OrderDeliverPlace;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.domain.DifficultDeliverAddress;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.repository.DifficultDeliverAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class DifficultDeliverAddressQueryService {
    private final DifficultDeliverAddressRepository difficultDeliverAddressRepository;

    public List<DifficultDeliverAddress> selectDifficultDeliverAddressWithProductId(Integer productId) {
        return difficultDeliverAddressRepository.findAllByProductId(productId);
    }
    
    public List<String> getDifficultDeliveryBcodes(Integer productId) {
        List<DifficultDeliverAddress> difficultDeliverAddresses = selectDifficultDeliverAddressWithProductId(productId);
        return difficultDeliverAddresses.stream().map(v -> v.getBcode()).toList();
    }

    public boolean canDeliver(Integer productId,
                              OrderDeliverPlace orderDeliverPlace) {
        List<String> difficultDeliveryBcodes = getDifficultDeliveryBcodes(productId);
        String orderDeliveryPlaceAreaCode = orderDeliverPlace.getBcode().substring(0, 5);

        for (String difficultDeliveryBcode : difficultDeliveryBcodes) {
            if (difficultDeliveryBcode.substring(0, 5)
                    .equals(orderDeliveryPlaceAreaCode)) {
//                throw new BusinessException("배송지에 배송 불가능한 상품이 포함돼 있습니다.");
                return false;
            }
        }
        return true;
    }
}
