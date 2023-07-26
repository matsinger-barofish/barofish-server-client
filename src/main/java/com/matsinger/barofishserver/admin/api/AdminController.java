package com.matsinger.barofishserver.admin.api;

import com.matsinger.barofishserver.admin.application.AdminCommandService;
import com.matsinger.barofishserver.admin.application.AdminQueryService;
import com.matsinger.barofishserver.admin.domain.AdminOrderBy;
import com.matsinger.barofishserver.admin.domain.AdminState;
import com.matsinger.barofishserver.admin.domain.Admin;
import com.matsinger.barofishserver.admin.domain.AdminAuth;
import com.matsinger.barofishserver.admin.domain.AdminAuthority;
import com.matsinger.barofishserver.admin.dto.AddAdminReq;
import com.matsinger.barofishserver.admin.dto.UpdateAdminReq;
import com.matsinger.barofishserver.jwt.*;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.RegexConstructor;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminQueryService adminQueryService;
    private final AdminCommandService adminCommandService;

    private final JwtProvider jwtProvider;
    private final JwtService jwt;
    private final Common utils;
    private final RegexConstructor reg;

    @GetMapping("/list")
    public ResponseEntity<CustomResponse<Page<Admin>>> selectAdminList(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                       @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                                       @RequestParam(value = "take", required = false, defaultValue = "10") Integer take,
                                                                       @RequestParam(value = "orderby", required = false, defaultValue = "createdAt") AdminOrderBy orderBy,
                                                                       @RequestParam(value = "orderType", required = false, defaultValue = "DESC") Sort.Direction orderType,
                                                                       @RequestParam(value = "name", required = false) String name,
                                                                       @RequestParam(value = "tel", required = false) String tel,
                                                                       @RequestParam(value = "state", required = false) String state,
                                                                       @RequestParam(value = "createdAtS", required = false) Timestamp createdAtS,
                                                                       @RequestParam(value = "createdAtE", required = false) Timestamp createdAtE,
                                                                       @RequestParam(value = "accessUser", required = false) Boolean accessUser,
                                                                       @RequestParam(value = "accessProduct", required = false) Boolean accessProduct,
                                                                       @RequestParam(value = "accessOrder", required = false) Boolean accessOrder,
                                                                       @RequestParam(value = "accessSettlement", required = false) Boolean accessSettlement,
                                                                       @RequestParam(value = "accessBoard", required = false) Boolean accessBoard,
                                                                       @RequestParam(value = "accessPromotion", required = false) Boolean accessPromotion,
                                                                       @RequestParam(value = "accessSetting", required = false) Boolean accessSetting) {
        CustomResponse<Page<Admin>> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            PageRequest pageRequest = PageRequest.of(page, take, Sort.by(orderType, orderBy.label));
            Specification<Admin> spec = (root, query, builder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (name != null) predicates.add(builder.like(root.get("name"), "%" + name + "%"));
                if (tel != null) predicates.add(builder.like(root.get("tel"), "%" + tel + "%"));
                if (state != null)
                    predicates.add(builder.and(root.get("state").in(Arrays.stream(state.split(",")).map(AdminState::valueOf).toList())));
                if (createdAtS != null) predicates.add(builder.greaterThan(root.get("createdAt"), createdAtS));
                if (createdAtE != null) predicates.add(builder.lessThan(root.get("createdAt"), createdAtE));
                if (accessUser != null)
                    predicates.add(builder.equal(root.get("adminAuth").get("accessUser"), accessUser));
                if (accessProduct != null)
                    predicates.add(builder.equal(root.get("adminAuth").get("accessProduct"), accessProduct));
                if (accessOrder != null)
                    predicates.add(builder.equal(root.get("adminAuth").get("accessOrder"), accessOrder));
                if (accessSettlement != null)
                    predicates.add(builder.equal(root.get("adminAuth").get("accessSettlement"), accessSettlement));
                if (accessBoard != null)
                    predicates.add(builder.equal(root.get("adminAuth").get("accessBoard"), accessBoard));
                if (accessPromotion != null)
                    predicates.add(builder.equal(root.get("adminAuth").get("accessPromotion"), accessPromotion));
                if (accessSetting != null)
                    predicates.add(builder.equal(root.get("adminAuth").get("accessSetting"), accessSetting));
                return builder.and(predicates.toArray(new Predicate[0]));
            };
            Page<Admin> admins = adminQueryService.selectAdminList(pageRequest, spec);
            admins.forEach(v -> v.setPassword(null));
            res.setData(Optional.ofNullable(admins));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomResponse<Admin>> selectAdmin(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                             @PathVariable("id") Integer id) {
        CustomResponse<Admin> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Admin admin = adminQueryService.selectAdmin(id);
            admin.setPassword(null);
            res.setData(Optional.ofNullable(admin));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @GetMapping("/my-info")
    public ResponseEntity<CustomResponse<Admin>> selectAdminMyInfo(@RequestHeader(value = "Authorization") Optional<String> auth) {
        CustomResponse<Admin> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Integer adminId = tokenInfo.get().getId();
            Admin admin = adminQueryService.selectAdmin(adminId);
            admin.setPassword(null);
            res.setData(Optional.ofNullable(admin));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }



    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Admin>> addAdminByMaster(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @RequestPart(value = "data") AddAdminReq data) {
        CustomResponse<Admin> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Admin master = adminQueryService.selectAdmin(tokenInfo.get().getId());
            if (!master.getAuthority().equals(AdminAuthority.MASTER))
                return res.throwError("최고 관리자만 생성 가능합니다.", "NOT_ALLOWED");
            if (data.getLoginId() == null) return res.throwError("로그인 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Admin checkExist = adminQueryService.selectAdminByLoginId(data.getLoginId());
            if (checkExist != null) return res.throwError("이미 존재하는 아이디입니다.", "NOT_ALLOWED");
            if (data.getPassword() == null) return res.throwError("비밀번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            String password = BCrypt.hashpw(data.getPassword(), BCrypt.gensalt());
            String name = utils.validateString(data.getName(), 20L, "이름");
            if (!Pattern.matches(reg.tel, data.getTel())) return res.throwError("전화번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String tel = data.getTel().replaceAll("[^\\d]*", "");
            Admin
                    admin =
                    Admin.builder().loginId(data.getLoginId()).password(password).authority(AdminAuthority.MANAGER).state(
                            AdminState.ACTIVE).name(name).tel(tel).createdAt(utils.now()).build();
            AdminAuth
                    adminAuth =
                    AdminAuth.builder().adminId(admin.getId()).accessUser(data.getAccessUser() != null &&
                            data.getAccessUser()).accessProduct(data.getAccessProduct() != null &&
                            data.getAccessProduct()).accessOrder(data.getAccessOrder() != null &&
                            data.getAccessOrder()).accessSettlement(data.getAccessSettlement() != null &&
                            data.getAccessSettlement()).accessBoard(data.getAccessBoard() != null &&
                            data.getAccessBoard()).accessPromotion(data.getAccessPromotion() != null &&
                            data.getAccessPromotion()).accessSetting(data.getAccessSetting() != null &&
                            data.getAccessSetting()).build();
            admin = adminCommandService.addAdmin(admin);
            adminAuth = adminCommandService.upsertAdminAuth(adminAuth);
            admin.setPassword(null);
            res.setData(Optional.of(admin));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }



    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Admin>> updateAdminByMaster(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @PathVariable("id") Integer id,
                                                                     @RequestPart(value = "data") UpdateAdminReq data) {
        CustomResponse<Admin> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Admin master = adminQueryService.selectAdmin(tokenInfo.get().getId());
            if (!master.getAuthority().equals(AdminAuthority.MASTER))
                return res.throwError("최고 관리자의 경우 수정 가능합니다.", "NOT_ALLOWED");
            Admin admin = adminQueryService.selectAdmin(id);
            AdminAuth adminAuth = adminQueryService.selectAdminAuth(id);
            if (data.getPassword() != null) {
                String password = BCrypt.hashpw(data.getPassword(), BCrypt.gensalt());
                admin.setPassword(password);
            }
            if (data.getState() != null) admin.setState(data.getState());
            if (data.getName() != null) {
                String name = utils.validateString(data.getName(), 20L, "이름");
                admin.setName(name);
            }
            if (data.getTel() != null) {
                if (!Pattern.matches(reg.tel, data.getTel()))
                    return res.throwError("전화번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                String tel = data.getTel().replaceAll("[^\\d]*", "");
                admin.setTel(tel);
            }
            if (data.getAccessUser() != null)
                adminAuth.setAccessUser(data.getAccessUser() || admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.getAccessProduct() != null)
                adminAuth.setAccessProduct(data.getAccessProduct() || admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.getAccessOrder() != null)
                adminAuth.setAccessOrder(data.getAccessOrder() || admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.getAccessSettlement() != null) adminAuth.setAccessSettlement(data.getAccessSettlement() ||
                    admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.getAccessBoard() != null)
                adminAuth.setAccessBoard(data.getAccessBoard() || admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.getAccessPromotion() != null) adminAuth.setAccessPromotion(data.getAccessPromotion() ||
                    admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.getAccessSetting() != null)
                adminAuth.setAccessSetting(data.getAccessSetting() || admin.getAuthority().equals(AdminAuthority.MASTER));
            admin = adminCommandService.addAdmin(admin);
            adminCommandService.upsertAdminAuth(adminAuth);
            admin.setPassword(null);
            res.setData(Optional.ofNullable(admin));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @PostMapping("login")
    public ResponseEntity<CustomResponse<Jwt>> loginAdmin(@RequestPart String loginId, @RequestPart String password) {
        CustomResponse<Jwt> res = new CustomResponse<>();
        try {
            Admin admin = adminQueryService.selectAdminByLoginId(loginId);
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
