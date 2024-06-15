package io.github.seal90.kiss.gateway.auth;

import org.springframework.web.server.ServerWebExchange;

public class DenyAllAuthCheck implements AuthCheck {

    @Override
    public Boolean auth(ServerWebExchange request) {
        return false;
    }

}
