package io.github.seal90.kiss.gateway.auth;

import io.github.seal90.kiss.core.constant.AppConstant;
import io.github.seal90.kiss.gateway.login.service.impl.AuthCache;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.BitSet;
import java.util.Map;

public class AuthCacheAuthCheck implements AuthCheck {

    @Override
    public Boolean auth(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
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
        Route route = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route.getId();
        Map<String, Integer> pathAuthKeyToId = AuthCache.pathAuthKeyToId.get(routeId);
        if(null == pathAuthKeyToId) {
            return false;
        }
        Integer pathId = pathAuthKeyToId.get(pathAuthKey);
        if(null == pathId) {
            return false;
        }
        return authority.get(pathId);

    }
}
