package io.github.seal90.kiss.gateway.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;

public class DenyAllLimitAuth implements LimitAuth {

    @Override
    public Boolean auth(ServerHttpRequest request) {
        return false;
    }

}
