package com.matsinger.barofishserver.address.repository;

import com.matsinger.barofishserver.address.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Integer>, JpaSpecificationExecutor<Address> {
    List<Address> findAllByIdIn(List<Integer> ids);

    Address findByBcode(String bcode);
}
