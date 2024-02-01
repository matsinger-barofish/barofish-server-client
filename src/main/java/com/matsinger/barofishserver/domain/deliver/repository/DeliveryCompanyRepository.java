package com.matsinger.barofishserver.domain.deliver.repository;

import com.matsinger.barofishserver.domain.deliver.domain.DeliveryCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryCompanyRepository extends JpaRepository<DeliveryCompany, String> {

}
