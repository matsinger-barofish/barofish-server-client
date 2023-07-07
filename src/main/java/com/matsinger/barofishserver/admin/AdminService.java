package com.matsinger.barofishserver.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final AdminAuthRepository adminAuthRepository;

    public Optional<Admin> selectAdminOptional(Integer id) {
        try {
            return adminRepository.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public Admin selectAdminByLoginId(String loginId) {
        return adminRepository.findByLoginId(loginId);
    }

    public Admin addAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public Page<Admin> selectAdminList(PageRequest pageRequest, Specification<Admin> spec) {
        return adminRepository.findAll(spec, pageRequest);
    }

    public Admin selectAdmin(Integer adminId) {
        return adminRepository.findById(adminId).orElseThrow(() -> {
            throw new Error("관리자 정보를 찾을 수 없습니다.");
        });
    }

    //AdminAuth
    public AdminAuth selectAdminAuth(Integer adminId) {
        return adminAuthRepository.findById(adminId).orElseThrow(() -> {
            throw new Error("관리장 정보를 찾을 수 없습니다.");
        });
    }

    public AdminAuth upsertAdminAuth(AdminAuth auth) {
        return adminAuthRepository.save(auth);
    }
}
