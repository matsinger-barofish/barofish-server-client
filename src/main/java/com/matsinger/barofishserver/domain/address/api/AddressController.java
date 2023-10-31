package com.matsinger.barofishserver.domain.address.api;

import com.matsinger.barofishserver.domain.address.application.AddressQueryService;
import com.matsinger.barofishserver.domain.address.domain.Address;
import com.matsinger.barofishserver.utils.CustomResponse;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/address")
public class AddressController {
    private final AddressQueryService addressQueryService;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<List<Address>>> selectAddressList(@RequestParam(value = "keyword", required = false) String keyword) {
        CustomResponse<List<Address>> res = new CustomResponse<>();
        try {
            Specification<Address> spec = null;
            if (keyword != null) spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (keyword != null) predicates.add(builder.like(root.get("sido"), "%" + keyword + "%"));
                if (keyword != null) predicates.add(builder.like(root.get("sigungu"), "%" + keyword + "%"));
                return builder.or(predicates.toArray(new Predicate[0]));
            };
            List<Address> addresses = addressQueryService.selectAddressList(spec);
            res.setData(Optional.ofNullable(addresses));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }
}
