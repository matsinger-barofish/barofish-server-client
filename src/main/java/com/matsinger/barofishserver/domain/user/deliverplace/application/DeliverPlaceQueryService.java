package com.matsinger.barofishserver.domain.user.deliverplace.application;

import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import com.matsinger.barofishserver.domain.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliverPlaceQueryService {

    private final DeliverPlaceRepository deliverPlaceRepository;


    public DeliverPlace findById(Integer deliverPlaceId) {
        return deliverPlaceRepository.findById(deliverPlaceId)
                .orElseThrow(() -> new BusinessException("배송지를 입력해주세요."));
    }
}
