package com.matsinger.barofishserver.domain.admin.application;

import com.matsinger.barofishserver.domain.admin.domain.Admin;
import com.matsinger.barofishserver.domain.admin.domain.AdminAuth;
import com.matsinger.barofishserver.domain.admin.repository.AdminAuthRepository;
import com.matsinger.barofishserver.domain.admin.repository.AdminRepository;
import com.matsinger.barofishserver.global.exception.BusinessException;
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
public class AdminQueryService {
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

    public Page<Admin> selectAdminList(PageRequest pageRequest, Specification<Admin> spec) {
        return adminRepository.findAll(spec, pageRequest);
    }

    public Admin selectAdmin(Integer adminId) {
        return adminRepository.findById(adminId).orElseThrow(() -> {
            throw new BusinessException("관리자 정보를 찾을 수 없습니다.");
        });
    }

    //AdminAuth
    public AdminAuth selectAdminAuth(Integer adminId) {
        return adminAuthRepository.findById(adminId).orElseThrow(() -> {
            throw new BusinessException("관리장 정보를 찾을 수 없습니다.");
        });
    }
}
