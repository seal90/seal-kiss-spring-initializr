package io.github.seal90.kiss.gateway.auth;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

public interface AuthCheck {

    Boolean auth(ServerWebExchange exchange);
}
