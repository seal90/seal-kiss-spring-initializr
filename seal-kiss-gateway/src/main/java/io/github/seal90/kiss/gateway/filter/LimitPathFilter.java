package io.github.seal90.kiss.gateway.filter;

import io.github.seal90.kiss.gateway.auth.LimitAuth;
import io.github.seal90.kiss.gateway.config.LimitConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.*;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class LimitPathFilter implements GlobalFilter, Ordered {

    private LimitConfigurationProperties limitConfigurationProperties;

    private LimitAuth limitAuth;

    public LimitPathFilter(LimitConfigurationProperties limitConfigurationProperties, LimitAuth limitAuth) {
        this.limitConfigurationProperties = limitConfigurationProperties;
        this.limitAuth = limitAuth;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod httpMethod = request.getMethod();
        RequestPath requestPath = request.getPath();

        Map<String, LimitConfigurationProperties.LimitRule> routeRules = limitConfigurationProperties.getRouteRules();
        if(null == routeRules) {
            log.info("未配置路径限制规则");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        LimitConfigurationProperties.LimitRule limitRule = routeRules.get(route.getId());
        if(null == limitRule) {
            log.info("未配置路径限制规则，路由id: {}", route.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if(limitRule.getAllAllow()) {
            return chain.filter(exchange);
        }
        Map<String, Boolean> pathAuth = limitRule.getPathAuth();
        if(null == pathAuth) {
            log.info("未配置路径限制规则下的路径规则，路由id: {}, ", route.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        String pathAuthKey = httpMethod.name() + "_" + requestPath;
        Boolean needAuth = pathAuth.get(pathAuthKey);
        if(null == needAuth) {
            log.info("未配置路径限制规则下的路径规则，路由id: {}, 路径：{} ", route.getId(), pathAuthKey);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        if(needAuth) {
            boolean auth = limitAuth.auth(request);
            if(!auth) {
                log.info("认证未通过，路由id: {}, 路径：{} ", route.getId(), pathAuthKey);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
