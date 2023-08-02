package com.matsinger.barofishserver.admin.log.repository;

import com.matsinger.barofishserver.admin.log.domain.AdminLog;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminLogRepository extends JpaRepository<AdminLog, String>, JpaSpecificationExecutor<AdminLog> {
    @Query(value = "select concat(date_format(current_timestamp()+interval 9 hour,'%y%m%d%H%i%s'),lpad(nextval" +
            "(admin_log_seq),4,'0')) id", nativeQuery = true)
    Tuple getAdminLogId();
}

