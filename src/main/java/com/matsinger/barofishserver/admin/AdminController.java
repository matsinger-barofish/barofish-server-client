package com.matsinger.barofishserver.admin;

import com.matsinger.barofishserver.banner.BannerType;
import com.matsinger.barofishserver.jwt.*;
import com.matsinger.barofishserver.store.object.StoreState;
import com.matsinger.barofishserver.utils.Common;
import com.matsinger.barofishserver.utils.CustomResponse;
import com.matsinger.barofishserver.utils.RegexConstructor;
import jakarta.persistence.criteria.Predicate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
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
    private final AdminService adminService;

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
            Page<Admin> admins = adminService.selectAdminList(pageRequest, spec);
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
            Admin admin = adminService.selectAdmin(id);
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
            Admin admin = adminService.selectAdmin(adminId);
            admin.setPassword(null);
            res.setData(Optional.ofNullable(admin));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class AddAdminReq {
        String loginId;
        String password;
        String name;
        String tel;
        Boolean accessUser;
        Boolean accessProduct;
        Boolean accessOrder;
        Boolean accessSettlement;
        Boolean accessBoard;
        Boolean accessPromotion;
        Boolean accessSetting;
    }

    @PostMapping("/add")
    public ResponseEntity<CustomResponse<Admin>> addAdminByMaster(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                  @RequestPart(value = "data") AddAdminReq data) {
        CustomResponse<Admin> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Admin master = adminService.selectAdmin(tokenInfo.get().getId());
            if (!master.getAuthority().equals(AdminAuthority.MASTER))
                return res.throwError("최고 관리자만 생성 가능합니다.", "NOT_ALLOWED");
            if (data.loginId == null) return res.throwError("로그인 아이디를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            Admin checkExist = adminService.selectAdminByLoginId(data.loginId);
            if (checkExist != null) return res.throwError("이미 존재하는 아이디입니다.", "NOT_ALLOWED");
            if (data.password == null) return res.throwError("비밀번호를 입력해주세요.", "INPUT_CHECK_REQUIRED");
            String password = BCrypt.hashpw(data.password, BCrypt.gensalt());
            String name = utils.validateString(data.name, 20L, "이름");
            if (!Pattern.matches(reg.tel, data.tel)) return res.throwError("전화번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
            String tel = data.tel.replaceAll("[^\\d]*", "");
            Admin
                    admin =
                    Admin.builder().loginId(data.loginId).password(password).authority(AdminAuthority.MANAGER).state(
                            AdminState.ACTIVE).name(name).tel(tel).createdAt(utils.now()).build();
            AdminAuth
                    adminAuth =
                    AdminAuth.builder().accessUser(data.accessUser != null &&
                            data.accessUser).accessProduct(data.accessProduct != null &&
                            data.accessProduct).accessOrder(data.accessOrder != null &&
                            data.accessOrder).accessSettlement(data.accessSettlement != null &&
                            data.accessSettlement).accessBoard(data.accessBoard != null &&
                            data.accessBoard).accessPromotion(data.accessPromotion != null &&
                            data.accessPromotion).accessSetting(data.accessSetting != null &&
                            data.accessSetting).build();
            admin = adminService.addAdmin(admin);
            adminAuth.setAdminId(admin.getId());
            adminAuth = adminService.upsertAdminAuth(adminAuth);
            admin.setPassword(null);
            res.setData(Optional.of(admin));
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return res.defaultError(e);
        }
    }

    @Getter
    @NoArgsConstructor
    private static class UpdateAdminReq {
        String password;
        AdminState state;
        String name;
        String tel;
        Boolean accessUser;
        Boolean accessProduct;
        Boolean accessOrder;
        Boolean accessSettlement;
        Boolean accessBoard;
        Boolean accessPromotion;
        Boolean accessSetting;
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<CustomResponse<Admin>> updateAdminByMaster(@RequestHeader(value = "Authorization") Optional<String> auth,
                                                                     @PathVariable("id") Integer id,
                                                                     @RequestPart(value = "data") UpdateAdminReq data) {
        CustomResponse<Admin> res = new CustomResponse<>();
        Optional<TokenInfo> tokenInfo = jwt.validateAndGetTokenInfo(Set.of(TokenAuthType.ADMIN), auth);
        if (tokenInfo == null) return res.throwError("인증이 필요합니다.", "FORBIDDEN");
        try {
            Admin master = adminService.selectAdmin(tokenInfo.get().getId());
            if (!master.getAuthority().equals(AdminAuthority.MASTER))
                return res.throwError("최고 관리자의 경우 수정 가능합니다.", "NOT_ALLOWED");
            Admin admin = adminService.selectAdmin(id);
            AdminAuth adminAuth = adminService.selectAdminAuth(id);
            if (data.password != null) {
                String password = BCrypt.hashpw(data.password, BCrypt.gensalt());
                admin.setPassword(password);
            }
            if (data.state != null) admin.setState(data.state);
            if (data.name != null) {
                String name = utils.validateString(data.name, 20L, "이름");
                admin.setName(name);
            }
            if (data.tel != null) {
                if (!Pattern.matches(reg.tel, data.tel))
                    return res.throwError("전화번호 형식을 확인해주세요.", "INPUT_CHECK_REQUIRED");
                String tel = data.tel.replaceAll("[^\\d]*", "");
                admin.setTel(tel);
            }
            if (data.accessUser != null)
                adminAuth.setAccessUser(data.accessUser || admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.accessProduct != null)
                adminAuth.setAccessProduct(data.accessProduct || admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.accessOrder != null)
                adminAuth.setAccessOrder(data.accessOrder || admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.accessSettlement != null) adminAuth.setAccessSettlement(data.accessSettlement ||
                    admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.accessBoard != null)
                adminAuth.setAccessBoard(data.accessBoard || admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.accessPromotion != null) adminAuth.setAccessPromotion(data.accessPromotion ||
                    admin.getAuthority().equals(AdminAuthority.MASTER));
            if (data.accessSetting != null)
                adminAuth.setAccessSetting(data.accessSetting || admin.getAuthority().equals(AdminAuthority.MASTER));
            admin = adminService.addAdmin(admin);
            adminService.upsertAdminAuth(adminAuth);
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
