package io.github.seal90.kiss.gateway.filter.factory;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import lombok.Data;

import reactor.core.publisher.Mono;

import static io.github.seal90.kiss.gateway.support.SealServerWebExchangeUtils.GATEWAY_ALREADY_GRAY_ATTR;

@ConfigurationProperties("spring.cloud.gateway.static-gray")
public class StaticGrayGatewayFilterFactory extends AbstractGatewayFilterFactory<StaticGrayGatewayFilterFactory.Config>{
    
    public static final String GRAY_PATH_KEY = "grayPath";

    public static final String GRAY_KEY_FROM_KEY = "grayKeyFromType";

    public static final String HEADER_KEY_KEY = "headerKey";

    public static final String IN_KEY = "in";
    
    public StaticGrayGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
	public List<String> shortcutFieldOrder() {
		return Arrays.asList(GRAY_PATH_KEY, GRAY_KEY_FROM_KEY, HEADER_KEY_KEY, IN_KEY);
	}

    @Override
	public GatewayFilter apply(Config config) {
        return new GatewayFilter() {

            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                boolean alreadyGray = exchange.getAttributeOrDefault(GATEWAY_ALREADY_GRAY_ATTR, false);
				if (alreadyGray) {
					return chain.filter(exchange);
				}
				exchange.getAttributes().put(GATEWAY_ALREADY_GRAY_ATTR, true);

                ServerHttpRequest request = exchange.getRequest();
                HttpHeaders headers = request.getHeaders();
                String grayFlag = headers.getFirst(config.getHeaderKey());
                if(config.getIn().contains(grayFlag)) {
                    String originPath = request.getURI().getRawPath();
                    String newPathStr = originPath;
                    if(originPath.lastIndexOf("/") > 0) {
                        String tempPath = originPath.substring(1);
                        String firstPath = tempPath.substring(0, tempPath.indexOf("/"));

                        String lastTempPath = originPath.substring(1);
                        newPathStr = "/"+firstPath+"/"+config.getGrayPath()+lastTempPath.substring(lastTempPath.indexOf("/"));
                    } else {
                        newPathStr = "/"+config.getGrayPath() + originPath;
                    }
                    
                    ServerHttpRequest newServerHttpRequest = request.mutate().path(newPathStr).build();

                    return chain.filter(exchange.mutate().request(newServerHttpRequest).build());
                    
                }
                
                return chain.filter(exchange);
            }


        };
    }

    @Data
    public static class Config {
        private String grayPath;
        private GrayKeyFromType grayKeyFromType;
        private String headerKey;
        private GrayCalType grayCalType;
        private List<String> in;
	}

    public static enum GrayKeyFromType {
        BY_HEADER,
        // BY_USER,
    }

    public static enum GrayCalType {
        IN,
        // NUM_THOUSANDTH,
        // STR_HASH_THOUSANDTH,
    }
}
