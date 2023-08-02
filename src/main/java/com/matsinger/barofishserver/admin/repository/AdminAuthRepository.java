package com.matsinger.barofishserver.admin.repository;

import com.matsinger.barofishserver.admin.domain.AdminAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminAuthRepository extends JpaRepository<AdminAuth, Integer>, JpaSpecificationExecutor<AdminAuth> {
}
