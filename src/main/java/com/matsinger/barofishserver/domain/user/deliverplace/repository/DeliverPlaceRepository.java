package com.matsinger.barofishserver.domain.user.deliverplace.repository;

import com.matsinger.barofishserver.domain.user.deliverplace.DeliverPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliverPlaceRepository extends JpaRepository<DeliverPlace, Integer> {

    List<DeliverPlace> findAllByUserId(Integer userId);

    Optional<DeliverPlace> findByUserIdAndIsDefault(Integer userId, Boolean isDefault);

    void deleteAllByUserIdIn(List<Integer> userIds);
}
