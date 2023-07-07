package com.matsinger.barofishserver.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdminAuthRepository extends JpaRepository<AdminAuth, Integer>, JpaSpecificationExecutor<AdminAuth> {
}
