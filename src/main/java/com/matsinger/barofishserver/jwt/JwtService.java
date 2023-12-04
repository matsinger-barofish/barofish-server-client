package com.matsinger.barofishserver.jwt;

import com.matsinger.barofishserver.domain.admin.application.AdminQueryService;
import com.matsinger.barofishserver.domain.store.application.StoreService;
import com.matsinger.barofishserver.domain.user.application.UserCommandService;
import com.matsinger.barofishserver.global.error.ErrorCode;
import com.matsinger.barofishserver.jwt.exception.JwtBusinessException;
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

    public TokenInfo validateAndGetTokenInfo(Set<TokenAuthType> authTypesToAllow, Optional<String> authorizationString) {

        if (authTypesToAllow.contains(TokenAuthType.ALLOW) && authorizationString.isEmpty()) {
            return new TokenInfo(null, TokenAuthType.ALLOW);
        }

        if (!authTypesToAllow.contains(TokenAuthType.ALLOW) && authorizationString.isEmpty()) {
            throw new JwtBusinessException(ErrorCode.TOKEN_REQUIRED);
        }

        String rawToken = authorizationString.get();

        if (!rawToken.startsWith("Bearer")) {
            throw new JwtBusinessException(ErrorCode.TOKEN_INVALID);
        }

        String token = rawToken.substring(7);

        if (!authTypesToAllow.contains(TokenAuthType.ALLOW) && isExpired(token)) {
            throw new JwtBusinessException(ErrorCode.TOKEN_EXPIRED);
        }
        if (authTypesToAllow.contains(TokenAuthType.ALLOW) && isExpired(token)) {
            return null;
        }

        TokenInfo tokenInfo = extractIdAndAuthType(token);

        // 토큰 기한이 만료 됐더라도 접근 권한이 모두에게 허용이면 통과
//        if (authTypesToAllow.contains(TokenAuthType.ALLOW)) {
//            tokenInfo.setId(null);
//            tokenInfo.setType(TokenAuthType.ALLOW);
//        }

        if (tokenInfo.getId() == null && tokenInfo.getType() != TokenAuthType.ALLOW) {
            throw new JwtBusinessException(ErrorCode.TOKEN_INVALID);
        }

        if (!authTypesToAllow.contains(tokenInfo.getType())) {
            throw new JwtBusinessException(ErrorCode.NOT_ALLOWED);
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
