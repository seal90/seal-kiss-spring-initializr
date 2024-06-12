package io.github.seal90.kiss.gateway.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;

public class AllowAllLimitAuth implements LimitAuth {

    @Override
    public Boolean auth(ServerHttpRequest request) {
        return true;
    }
}
