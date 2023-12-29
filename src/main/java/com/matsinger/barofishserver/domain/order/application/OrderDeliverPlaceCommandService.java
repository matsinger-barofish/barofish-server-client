package com.matsinger.barofishserver.domain.order.application;

import com.matsinger.barofishserver.domain.order.orderprductinfo.domain.OrderProductInfo;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.domain.DifficultDeliverAddress;
import com.matsinger.barofishserver.domain.product.difficultDeliverAddress.repository.DifficultDeliverAddressRepository;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.domain.user.deliverplace.application.DeliverPlaceService;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDeliverPlaceCommandService {

    private final DifficultDeliverAddressRepository difficultDeliverAddressRepository;
    private final DeliverPlaceService deliverPlaceService;

    public void save(List<OrderProductInfo> orderProductInfos, Integer deliverPlaceId) {
        DeliverPlace deliverPlace = deliverPlaceService.selectDeliverPlace(deliverPlaceId);

        for (OrderProductInfo orderProductInfo : orderProductInfos) {
            List<String> difficultDeliveryBcodes = difficultDeliverAddressRepository
                    .findAllByProductId(orderProductInfo.getProductId())
                    .stream().map(DifficultDeliverAddress::getBcode).toList();

            if (isDeliveryPlaceContainsDifficultDeliveryRegion(difficultDeliveryBcodes, deliverPlace)) {
                throw new BusinessException("배송지에 배송 불가능한 상품이 포함돼 있습니다.");
            }
        }
    }

    private boolean isDeliveryPlaceContainsDifficultDeliveryRegion(List<String> difficultDeliveryBcodes, DeliverPlace deliverPlace) {
        return difficultDeliveryBcodes.stream().anyMatch(
                bcode -> bcode.length() >= 5 &&
                        bcode.substring(0, 5).equals(deliverPlace.getBcode().substring(0, 5)));
    }
}
