package com.matsinger.barofishserver.domain.admin.application;

import com.matsinger.barofishserver.domain.admin.domain.Admin;
import com.matsinger.barofishserver.domain.admin.domain.AdminAuth;
import com.matsinger.barofishserver.domain.admin.repository.AdminAuthRepository;
import com.matsinger.barofishserver.domain.admin.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminCommandService {
    private final AdminRepository adminRepository;
    private final AdminAuthRepository adminAuthRepository;

    public Admin addAdmin(Admin admin) {
        return adminRepository.save(admin);
    }



    public AdminAuth upsertAdminAuth(AdminAuth auth) {
        return adminAuthRepository.save(auth);
    }
}
