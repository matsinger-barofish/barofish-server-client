package com.matsinger.barofishserver.domain.admin.repository;

import com.matsinger.barofishserver.domain.admin.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer>, JpaSpecificationExecutor<Admin> {
    Admin findByLoginId(String loginId);
}
