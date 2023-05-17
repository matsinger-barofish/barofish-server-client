package com.matsinger.barofishserver.jwt;

import com.matsinger.barofishserver.admin.Admin;
import com.matsinger.barofishserver.admin.AdminService;
import com.matsinger.barofishserver.admin.AdminState;
import com.matsinger.barofishserver.store.Store;
import com.matsinger.barofishserver.store.StoreService;
import com.matsinger.barofishserver.store.StoreState;
import com.matsinger.barofishserver.user.User;
import com.matsinger.barofishserver.user.UserService;
import com.matsinger.barofishserver.user.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtProvider jwtProvider;

    private final UserService userService;
    private final AdminService adminService;
    private final StoreService storeService;

    public Optional<TokenInfo> validateAndGetTokenInfo(Set<TokenAuthType> allowAuth,
                                                       Optional<String> authorizationString) {
        TokenInfo info = new TokenInfo();
        if (!allowAuth.contains(TokenAuthType.ALLOW)) {
            if (!authorizationString.isPresent()) return null;
            if (!authorizationString.get().startsWith("Bearer")) return null;
        }

        String token = authorizationString.get().substring(7);

        if (allowAuth.contains(TokenAuthType.ALLOW)) {
            info.setId(Integer.valueOf(jwtProvider.getIdFromToken(token)));
            info.setType(jwtProvider.getTypeFromToken(token));
            return Optional.of(info);
        } else {
            TokenAuthType auth = jwtProvider.getTypeFromToken(token);
            Integer id = jwtProvider.getIdFromToken(token);
            if (id == null || !allowAuth.contains(auth)) return null;
            if (auth.equals(TokenAuthType.USER)) {
                //TODO: 사용자의 상태에 따른 유효성 검증
                Optional<User> user = userService.selectUserOptional(id);
                if (!user.isPresent() || !user.get().getState().equals(UserState.ACTIVE)) {
                    return null;
                }
                info.setType(auth);
                info.setId(id);
                return Optional.of(info);
            } else if (auth.equals(TokenAuthType.PARTNER)) {
                //TODO: 파트터의 상태에 따른 유효성 검증
                Optional<Store> partner = storeService.selectStoreOptional(id);
                if (!partner.isPresent() || !partner.get().getState().equals(StoreState.ACTIVE)) {
                    return null;
                }
                info.setType(auth);
                info.setId(id);
                return Optional.of(info);
            } else if (auth.equals(TokenAuthType.ADMIN)) {
                //TODO: 관리자의 상태에 따른 유효성 검증
                Optional<Admin> admin = adminService.selectAdminOptional(id);
                if (!admin.isPresent() || !admin.get().getState().equals(AdminState.ACTIVE)) {
                    return null;
                }
                info.setType(auth);
                info.setId(id);
                return Optional.of(info);
            }
        }
        return Optional.of(info);
    }
}
