package com.matsinger.barofishserver.domain.admin.log.application;

import com.matsinger.barofishserver.domain.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.domain.admin.log.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminLogQueryService {
    private final AdminLogRepository adminLogRepository;

    public Page<AdminLog> selectAdminLogList(Specification<AdminLog> spec, Pageable pageable) {
        return adminLogRepository.findAll(spec, pageable);
    }

    public String getAdminLogId() {
        return adminLogRepository.getAdminLogId()
                .get("id")
                .toString();
    }
}
