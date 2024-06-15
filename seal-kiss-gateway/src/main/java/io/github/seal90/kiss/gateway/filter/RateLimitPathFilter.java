package io.github.seal90.kiss.gateway.filter;

import com.google.common.util.concurrent.RateLimiter;
import io.github.seal90.kiss.gateway.config.RateLimitPathConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimitPathFilter implements GlobalFilter, Ordered {

    private RateLimitPathConfigurationProperties rateLimitPathConfigurationProperties;

    private ConcurrentHashMap<String, RateLimiter> pathRateLimiters = new ConcurrentHashMap<>();

    public RateLimitPathFilter(RateLimitPathConfigurationProperties rateLimitPathConfigurationProperties) {
        this.rateLimitPathConfigurationProperties = rateLimitPathConfigurationProperties;
    }

    /**
     * 使用 redis 可以参考 RedisRateLimiter 中基于 lua 的实现
     * @param exchange the current server exchange
     * @param chain provides a way to delegate to the next filter
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        Route route = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        HttpMethod httpMethod = request.getMethod();
        RequestPath requestPath = request.getPath();
        String pathKey = httpMethod.name() + "_" + requestPath;
        String routePathKey = route.getId() + "_" + pathKey;

        Tuple2<Boolean, RateLimitPathConfigurationProperties.PathRate> findPathRate = findPathRate(pathKey, route);
        if(!findPathRate.getT1()) {
            return chain.filter(exchange);
        }

        RateLimitPathConfigurationProperties.PathRate pathRate = findPathRate.getT2();

        // How many requests per second do you want a user to be allowed to do?
        int replenishRate = pathRate.getReplenishRate();

        // How much bursting do you want to allow?
        int burstCapacity = pathRate.getBurstCapacity();

        // How many tokens are requested per request?
        int requestedTokens = pathRate.getRequestedTokens();

//        // The arguments to the LUA script. time() returns unixtime in seconds.
//        List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "", "", requestedTokens + "");
//        // allowed, tokens_left = redis.eval(SCRIPT, keys, args)
//        // script -> GatewayRedisAutoConfiguration#redisRequestRateLimiterScript
//        Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys, scriptArgs);

        RateLimiter pathRateLimiter = pathRateLimiters.get(routePathKey);
        if(null == pathRateLimiter) {
            RateLimiter newPathRateLimiter = RateLimiter.create(replenishRate);
            newPathRateLimiter.setRate(burstCapacity);
            RateLimiter savedPathRateLimiter = pathRateLimiters.putIfAbsent(routePathKey, newPathRateLimiter);
            if(null != savedPathRateLimiter) {
                pathRateLimiter = savedPathRateLimiter;
            } else {
                pathRateLimiter = newPathRateLimiter;
            }
        }

        boolean acquire = pathRateLimiter.tryAcquire(requestedTokens);
        if(acquire) {
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        return response.setComplete();
    }


    private Tuple2<Boolean, RateLimitPathConfigurationProperties.PathRate> findPathRate(String pathKey, Route route) {

        RateLimitPathConfigurationProperties.PathRate defaultRate = rateLimitPathConfigurationProperties.getDefaultRate();
        Map<String, RateLimitPathConfigurationProperties.RouteRate> routeRates = rateLimitPathConfigurationProperties.getRouteRates();
        if(null == routeRates) {
            return Tuples.of(true, defaultRate);
        }
        RateLimitPathConfigurationProperties.RouteRate routeRate = routeRates.get(route.getId());
        if(null == routeRate) {
            return Tuples.of(true, defaultRate);
        }
        if(routeRate.isNoneLimit()) {
            return Tuples.of(false, defaultRate);
        }
        Map<String, RateLimitPathConfigurationProperties.PathRate> pathRateMap = routeRate.getPathRateMap();
        if(null == pathRateMap) {
            return Tuples.of(true, defaultRate);
        }
        RateLimitPathConfigurationProperties.PathRate pathRate = pathRateMap.get(pathKey);
        if(null == pathRate) {
            return Tuples.of(true, defaultRate);
        }
        return Tuples.of(true, pathRate);

    }

    static List<String> getKeys(String id) {
        // use `{}` around keys to use Redis Key hash tags
        // this allows for using redis cluster

        // Make a unique key per user.
        String prefix = "request_rate_path_limiter.{" + id;

        // You need two Redis keys for Token Bucket.
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    @Override
    public int getOrder() {
        return 50;
    }
}
