package com.matsinger.barofishserver.user;

import com.matsinger.barofishserver.user.object.DeliverPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliverPlaceRepository extends JpaRepository<DeliverPlace, Integer> {

    public List<DeliverPlace> findAllByUserId(Integer userId);

    public DeliverPlace findByUserIdAndIsDefault(Integer userId, Boolean isDefault);
}
