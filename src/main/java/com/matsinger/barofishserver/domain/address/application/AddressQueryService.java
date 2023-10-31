package com.matsinger.barofishserver.domain.address.application;

import com.matsinger.barofishserver.domain.address.domain.Address;
import com.matsinger.barofishserver.domain.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class AddressQueryService {
    private final AddressRepository addressRepository;

    public List<Address> selectAddressList(Specification<Address> spec) {
        if (spec == null) return addressRepository.findAll();
        else return addressRepository.findAll(spec);
    }

    public List<Address> selectAddressListWithIds(List<Integer> ids) {
        return addressRepository.findAllByIdIn(ids);
    }

    public Address selectAddressWithBcode(String bcode) {
        return addressRepository.findByBcode(bcode);
    }
}
