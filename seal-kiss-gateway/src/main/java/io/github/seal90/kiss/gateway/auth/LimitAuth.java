package io.github.seal90.kiss.gateway.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;

public interface LimitAuth {

    Boolean auth(ServerHttpRequest request);
}
