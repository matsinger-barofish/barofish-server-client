package com.matsinger.barofishserver.jwt;

import com.matsinger.barofishserver.domain.admin.application.AdminQueryService;
import com.matsinger.barofishserver.domain.admin.domain.Admin;
import com.matsinger.barofishserver.domain.admin.domain.AdminState;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.domain.Store;
import com.matsinger.barofishserver.domain.store.domain.StoreState;
import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.user.domain.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class JwtService {

    private final JwtProvider jwtProvider;

    private final UserCommandService userService;
    private final AdminQueryService adminService;
    private final StoreService storeService;

    public Optional<TokenInfo> validateAndGetTokenInfo2(Set<TokenAuthType> allowAuth,
            Optional<String> authorizationString) {
        TokenInfo info = new TokenInfo();
        String token = authorizationString.map(s -> s.substring(7)).orElse(null);

        if (!allowAuth.contains(TokenAuthType.ALLOW)) {
            if (authorizationString.isEmpty())
                return null;
            if (!authorizationString.get().startsWith("Bearer"))
                return null;
        }

        if (authorizationString == null || authorizationString.isEmpty()) {
            info.setId(null);
            info.setType(TokenAuthType.ALLOW);
            return Optional.of(info);
        }

        if (allowAuth.contains(TokenAuthType.ALLOW)) {
            info.setId(jwtProvider.getIdFromToken(token));
            info.setType(jwtProvider.getTypeFromToken(token));
            return Optional.of(info);
        }

        TokenAuthType auth = TokenAuthType.ALLOW;
        Integer id = null;
        try {
            auth = jwtProvider.getTypeFromToken(token);
            id = jwtProvider.getIdFromToken(token);
        } catch (Exception e) {
            return null;
        }
        if (id == null || !allowAuth.contains(auth)) {
            return null;
        }

        if (auth.equals(TokenAuthType.USER)) {
            // TODO: 사용자의 상태에 따른 유효성 검증
            Optional<User> user = userService.selectUserOptional(id);
            if (!user.isPresent() || !user.get().getState().equals(UserState.ACTIVE)) {
                return null;
            }
        } else if (auth.equals(TokenAuthType.PARTNER)) {
            // TODO: 파트너의 상태에 따른 유효성 검증
            Optional<Store> partner = storeService.selectStoreOptional(id);
            if (!partner.isPresent() || !partner.get().getState().equals(StoreState.ACTIVE)) {
                return null;
            }
        } else if (auth.equals(TokenAuthType.ADMIN)) {
            // TODO: 관리자의 상태에 따른 유효성 검증
            Optional<Admin> admin = adminService.selectAdminOptional(id);
            if (admin == null || !admin.isPresent() || !admin.get().getState().equals(AdminState.ACTIVE)) {
                return null;
            }
        }
        info.setType(auth);
        info.setId(id);
        return Optional.of(info);
    }

    public Optional<TokenInfo> validateAndGetTokenInfo(Set<TokenAuthType> allowAuth,
            Optional<String> authorizationString) {
        TokenInfo info = new TokenInfo();
        if (!allowAuth.contains(TokenAuthType.ALLOW)) {
            if (!authorizationString.isPresent())
                return null;
            if (!authorizationString.get().startsWith("Bearer"))
                return null;
        }

        String token = authorizationString.isPresent() ? authorizationString.get().substring(7) : null;

        if (allowAuth.contains(TokenAuthType.ALLOW)) {
            if (authorizationString == null || authorizationString.isEmpty()) {
                info.setId(null);
                info.setType(TokenAuthType.ALLOW);
                return Optional.of(info);
            }
            info.setId(jwtProvider.getIdFromToken(token));
            info.setType(jwtProvider.getTypeFromToken(token));
            return Optional.of(info);
        } else {
            TokenAuthType auth = TokenAuthType.ALLOW;
            Integer id = null;
            try {
                auth = jwtProvider.getTypeFromToken(token);
                id = jwtProvider.getIdFromToken(token);
            } catch (Exception e) {
                return null;
            }
            if (id == null || !allowAuth.contains(auth))
                return null;
            if (auth.equals(TokenAuthType.USER)) {
                // TODO: 사용자의 상태에 따른 유효성 검증
                Optional<User> user = userService.selectUserOptional(id);
                if (!user.isPresent() || !user.get().getState().equals(UserState.ACTIVE)) {
                    return null;
                }
                info.setType(auth);
                info.setId(id);
                return Optional.of(info);
            } else if (auth.equals(TokenAuthType.PARTNER)) {
                // TODO: 파트너의 상태에 따른 유효성 검증
                Optional<Store> partner = storeService.selectStoreOptional(id);
                if (!partner.isPresent() || !partner.get().getState().equals(StoreState.ACTIVE)) {
                    return null;
                }
                info.setType(auth);
                info.setId(id);
                return Optional.of(info);
            } else if (auth.equals(TokenAuthType.ADMIN)) {
                // TODO: 관리자의 상태에 따른 유효성 검증
                Optional<Admin> admin = adminService.selectAdminOptional(id);
                if (admin == null || !admin.isPresent() || !admin.get().getState().equals(AdminState.ACTIVE)) {
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
