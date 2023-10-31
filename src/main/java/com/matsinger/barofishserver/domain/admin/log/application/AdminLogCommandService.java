package com.matsinger.barofishserver.domain.admin.log.application;

import com.matsinger.barofishserver.domain.admin.log.domain.AdminLog;
import com.matsinger.barofishserver.domain.admin.log.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminLogCommandService {
    private final AdminLogRepository adminLogRepository;

    public AdminLog saveAdminLog(AdminLog adminLog) {
        return adminLogRepository.save(adminLog);
    }
}
