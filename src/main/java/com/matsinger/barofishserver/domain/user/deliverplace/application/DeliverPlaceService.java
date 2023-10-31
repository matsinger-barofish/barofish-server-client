package com.matsinger.barofishserver.domain.user.deliverplace.application;

import com.matsinger.barofishserver.domain.user.deliverplace.repository.DeliverPlaceRepository;
import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliverPlaceService {
    private final DeliverPlaceRepository deliverPlaceRepository;

    public DeliverPlace addDeliverPlace(DeliverPlace deliverPlace) {
        return deliverPlaceRepository.save(deliverPlace);
    }

    public DeliverPlace updateDeliverPlace(DeliverPlace deliverPlace) {
        return deliverPlaceRepository.save(deliverPlace);
    }

    public List<DeliverPlace> selectDeliverPlaceList(Integer userId) {
        return deliverPlaceRepository.findAllByUserId(userId);
    }

    public DeliverPlace selectDeliverPlace(Integer deliverPlaceId) {
        return deliverPlaceRepository.findById(deliverPlaceId).orElseThrow(() -> {
            throw new Error("배송지 정보를 찾을 수 없습니다.");
        });
    }

    public Optional<DeliverPlace> selectDefaultDeliverPlace(Integer userId) {
        return deliverPlaceRepository.findByUserIdAndIsDefault(userId, true);
    }

    public void deleteDeliverPlace(Integer id) {
        deliverPlaceRepository.deleteById(id);
    }
}
