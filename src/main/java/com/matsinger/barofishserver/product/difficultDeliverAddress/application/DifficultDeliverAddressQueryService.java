package com.matsinger.barofishserver.product.difficultDeliverAddress.application;

import com.matsinger.barofishserver.product.difficultDeliverAddress.domain.DifficultDeliverAddress;
import com.matsinger.barofishserver.product.difficultDeliverAddress.repository.DifficultDeliverAddressRepository;
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
}
