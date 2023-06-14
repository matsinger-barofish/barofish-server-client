package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.user.object.DeliverPlace;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliverPlaceService {
    private final DeliverPlaceRepository deliverPlaceRepository;

    DeliverPlace addDeliverPlace(DeliverPlace deliverPlace) {
        return deliverPlaceRepository.save(deliverPlace);
    }

    DeliverPlace updateDeliverPlace(DeliverPlace deliverPlace) {
        return deliverPlaceRepository.save(deliverPlace);
    }

    List<DeliverPlace> selectDeliverPlaceList(Integer userId) {
        return deliverPlaceRepository.findAllByUserId(userId);
    }

    DeliverPlace selectDeliverPlace(Integer deliverPlaceId) {
        return deliverPlaceRepository.findById(deliverPlaceId).orElseThrow(() -> {
            throw new Error("배송지 정보를 찾을 수 없습니다.");
        });
    }

    DeliverPlace selectDefaultDeliverPlace(Integer userId) {
        return deliverPlaceRepository.findByUserIdAndIsDefault(userId, true);
    }

    void deleteDeliverPlace(Integer id) {
        deliverPlaceRepository.deleteById(id);
    }
}
