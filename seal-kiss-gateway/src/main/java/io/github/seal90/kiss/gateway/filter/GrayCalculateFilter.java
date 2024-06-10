package io.github.seal90.kiss.gateway.filter;

import io.github.seal90.kiss.gateway.config.GrayConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static io.github.seal90.kiss.gateway.config.GrayConfigurationProperties.SEAL_GRAY_ENV_FLAG;
import static io.github.seal90.kiss.gateway.config.GrayConfigurationProperties.SEAL_GRAY_PATH_FLAG;
import static org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER;

public class GrayCalculateFilter implements GlobalFilter, Ordered {

    private GrayConfigurationProperties grayConfigurationProperties;

    public GrayCalculateFilter(GrayConfigurationProperties grayConfigurationProperties) {
        this.grayConfigurationProperties = grayConfigurationProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();

        boolean mutateFlag = false;
        if(!grayConfigurationProperties.getEnableClientRequestGrayHeader()) {
            mutateFlag = true;
            request = request.mutate().headers(toRemoveHeaders -> {
                toRemoveHeaders.remove(SEAL_GRAY_PATH_FLAG);
                toRemoveHeaders.remove(SEAL_GRAY_ENV_FLAG);
            }).build();
        }

        Route route = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route.getId();
        Map<String, GrayConfigurationProperties.GrayRule> routesRule = grayConfigurationProperties.getRoutesRule();
        if(null == routesRule) {
            if(mutateFlag) {
                return chain.filter(exchange.mutate().request(request).build());
            } else {
                return chain.filter(exchange);
            }
        }
        GrayConfigurationProperties.GrayRule rule = routesRule.get(routeId);
        if(null != rule) {
            boolean grayFlag = false;
            String calcVal = null;

            if(GrayConfigurationProperties.GrayKeyFromType.FROM_HEADER == rule.getKeyFromType()) {
                calcVal = headers.getFirst(GrayConfigurationProperties.GRAY_CALC_HEADER);
            }

            if(null != calcVal && GrayConfigurationProperties.GrayCalculateType.IN == rule.getCalculateType()) {
                List<String> inValues = rule.getInValues();
                grayFlag = !CollectionUtils.isEmpty(inValues) && inValues.contains(calcVal);
            }

            if (grayFlag) {
                if(GrayConfigurationProperties.GrayModifyType.GRAY_PATH == rule.getModifyType()) {
                    mutateFlag = true;
                    request = request.mutate().header(SEAL_GRAY_PATH_FLAG, rule.getTargetGrayFlag()).build();
                } else if (GrayConfigurationProperties.GrayModifyType.GRAY_ENV == rule.getModifyType()) {
                    mutateFlag = true;
                    request = request.mutate().header(SEAL_GRAY_ENV_FLAG, rule.getTargetGrayFlag()).build();
                }
            }
        }
        if(mutateFlag) {
            return chain.filter(exchange.mutate().request(request).build());
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ROUTE_TO_URL_FILTER_ORDER - 2;
    }
}
