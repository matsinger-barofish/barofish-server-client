package com.matsinger.barofishserver.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminService {
    private final AdminRepository adminRepository;

    public Optional<Admin> selectAdminOptional(Integer id) {
        try {
            return adminRepository.findById(id);
        } catch (Exception e) {
            return null;
        }
    }

    public Admin selectAdminByLoginId(String loginId){
        return adminRepository.findByLoginId(loginId);
    }
}
