package io.github.seal90.kiss.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static io.github.seal90.kiss.gateway.config.GrayConfigurationProperties.SEAL_GRAY_PATH_FLAG;
import static org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

public class GrayPathFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String grayPath = headers.getFirst(SEAL_GRAY_PATH_FLAG);
        if(StringUtils.hasText(grayPath)) {
            String originPath = request.getURI().getRawPath();
            String newPathStr;
            if(originPath.lastIndexOf("/") > 0) {
                String tempPath = originPath.substring(1);
                String firstPath = tempPath.substring(0, tempPath.indexOf("/"));

                newPathStr = "/" + firstPath + "/" + grayPath + tempPath.substring(tempPath.indexOf("/"));
            } else {
                newPathStr = "/" + grayPath + originPath;
            }
            ServerHttpRequest newServerHttpRequest = request.mutate().path(newPathStr).build();
//            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newServerHttpRequest.getURI());
            return chain.filter(exchange.mutate().request(newServerHttpRequest).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ROUTE_TO_URL_FILTER_ORDER - 1;
    }
}
