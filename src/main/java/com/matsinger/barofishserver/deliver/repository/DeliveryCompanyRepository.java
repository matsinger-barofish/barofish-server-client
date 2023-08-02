package com.matsinger.barofishserver.deliver.repository;

import com.matsinger.barofishserver.deliver.domain.DeliveryCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryCompanyRepository extends JpaRepository<DeliveryCompany, String> {

}
