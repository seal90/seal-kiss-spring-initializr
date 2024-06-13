package io.github.seal90.kiss.gateway.auth;

import io.github.seal90.kiss.core.constant.AppConstant;
import io.github.seal90.kiss.gateway.login.service.impl.AuthCache;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

import java.util.BitSet;

public class AuthCacheLimitAuth implements LimitAuth {

    @Override
    public Boolean auth(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String authKey = headers.getFirst(AppConstant.AUTH_HEADER_KEY);
        if(!StringUtils.hasText(authKey)) {
            return false;
        }

        String userId = AuthCache.authKeyToUser.get(authKey);
        if(!StringUtils.hasText(userId)) {
            return false;
        }

        BitSet authority = AuthCache.userToAuthority.get(userId);
        if(null == authority) {
            return false;
        }

        HttpMethod httpMethod = request.getMethod();
        RequestPath requestPath = request.getPath();

        String pathAuthKey = httpMethod.name() + "_" + requestPath;
        Integer pathId = AuthCache.pathAuthKeyToId.get(pathAuthKey);
        if(null == pathId) {
            return false;
        }
        return authority.get(pathId);

    }
}
