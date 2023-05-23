package com.matsinger.barofishserver.admin;

import com.matsinger.barofishserver.jwt.Jwt;
import com.matsinger.barofishserver.jwt.JwtProvider;
import com.matsinger.barofishserver.jwt.TokenAuthType;
import com.matsinger.barofishserver.utils.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    private final JwtProvider jwtProvider;

    @PostMapping("login")
    public ResponseEntity<CustomResponse<Jwt>> loginAdmin(@RequestPart String loginId, @RequestPart String password) {
        CustomResponse<Jwt> res = new CustomResponse<>();
        try {
            Admin admin = adminService.selectAdminByLoginId(loginId);
            if (admin == null) return res.throwError("아이디 및 비밀번호를 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (!BCrypt.checkpw(password, admin.getPassword()))
                return res.throwError("아이디 및 비밀번호를 확인해주세요.", "INPUT_CHECK_REQUIRED");
            if (!admin.getState().equals(AdminState.ACTIVE)) {
                if (admin.getState().equals(AdminState.BANNED)) return res.throwError("정지된 관리자입니다.", "NOT_ALLOWED");
                if (admin.getState().equals(AdminState.DELETED)) return res.throwError("삭제된 관리자입니다.", "NOT_ALLOWED");
            }
            String accessToken = jwtProvider.generateAccessToken(String.valueOf(admin.getId()), TokenAuthType.ADMIN);
            String refreshToken = jwtProvider.generateRefreshToken(String.valueOf(admin.getId()), TokenAuthType.ADMIN);
            Jwt token = new Jwt();
            token.setAccessToken(accessToken);
            token.setRefreshToken(refreshToken);
            res.setData(Optional.of(token));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }

    }
}
