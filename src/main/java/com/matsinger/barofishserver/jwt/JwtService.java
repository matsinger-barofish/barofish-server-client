package com.matsinger.barofishserver.jwt;

import com.matsinger.barofishserver.domain.admin.application.AdminQueryService;
import com.matsinger.barofishserver.domain.admin.domain.Admin;
import com.matsinger.barofishserver.domain.admin.domain.AdminState;
import com.matsinger.barofishserver.domain.store.domain.Store;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.store.domain.StoreState;
import com.matsinger.barofishserver.domain.user.domain.User;
import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.domain.user.domain.UserState;
import com.matsinger.barofishserver.jwt.exception.JwtExceptionMessage;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

    public TokenInfo validateAndGetTokenInfo(Set<TokenAuthType> authTypesToAllow, String authorizationString) {

        if (!authTypesToAllow.contains(TokenAuthType.ALLOW) && !authorizationString.startsWith("Bearer")) {
            throw new JwtException(JwtExceptionMessage.TOKEN_INVALID);
        }

        String token = authorizationString.substring(7);

        if (!authTypesToAllow.contains(TokenAuthType.ALLOW) && isExpired(token)) {
            throw new JwtException(JwtExceptionMessage.TOKEN_EXPIRED);
        }

        TokenInfo tokenInfo = extractIdAndAuthType(token);

        // 토큰 기한이 만료 됐더라도 접근 권한이 모두에게 허용이면 통과
        if (authTypesToAllow.contains(TokenAuthType.ALLOW)) {
            tokenInfo.setId(null);
            tokenInfo.setType(TokenAuthType.ALLOW);
        }

        if (tokenInfo.getId() == null && tokenInfo.getType() != TokenAuthType.ALLOW) {
            throw new JwtException(JwtExceptionMessage.TOKEN_INVALID);
        }

        if (!authTypesToAllow.contains(tokenInfo.getType())) {
            throw new IllegalArgumentException(JwtExceptionMessage.NOT_ALLOWED);
        }

        return tokenInfo;
    }

    @NotNull
    private TokenInfo extractIdAndAuthType(String token) {
        TokenInfo tokenInfo = new TokenInfo();
        TokenAuthType tokenAuthType = jwtProvider.getTypeFromToken(token);

        tokenInfo.setId(jwtProvider.getIdFromToken(token));
        if (tokenAuthType.equals(TokenAuthType.USER)) {
            tokenInfo.setType(TokenAuthType.USER);
        }
        if (tokenAuthType.equals(TokenAuthType.ADMIN)) {
            tokenInfo.setType(TokenAuthType.ADMIN);
        }
        if (tokenAuthType.equals(TokenAuthType.PARTNER)) {
            tokenInfo.setType(TokenAuthType.PARTNER);
        }
        return tokenInfo;
    }

    public boolean isExpired(String jwtToken) {
        return jwtProvider.isTokenExpired(jwtToken);
    }
}
