package io.github.seal90.kiss.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties("seal.kiss.rate-path-limit")
public class RateLimitPathConfigurationProperties {

    private PathRate defaultRate;

    private Map<String, RouteRate> routeRates;

    public RateLimitPathConfigurationProperties() {
        this.defaultRate = new PathRate();
        this.routeRates = new HashMap<>();
    }

    @Data
    public static class RouteRate {

        private boolean noneLimit;

        private List<PathRate> pathRates;

        private Map<String, PathRate> pathRateMap;

        public RouteRate() {
            this.noneLimit = false;
            this.pathRates = new ArrayList<>();
            this.pathRateMap = new HashMap<>();
        }

        public Map<String, PathRate> getPathRateMap() {
            if(CollectionUtils.isEmpty(this.pathRateMap) && !CollectionUtils.isEmpty(this.pathRates)) {
                this.pathRateMap = this.pathRates.stream().collect(Collectors.toMap(
                        pathRate -> pathRate.getMethod().name()+"_"+pathRate.getPath(),
                        Function.identity(), (key1, key2) -> key1));
            }
            return this.pathRateMap;
        }
    }

    @Data
    public static class PathRate {

        private HttpMethod method;

        private String path;

        private int replenishRate;

        private int burstCapacity;

        private int requestedTokens;

        public PathRate() {
            this.method = HttpMethod.POST;
            this.replenishRate = 100;
            this.burstCapacity = 100;
            this.requestedTokens = 1;
        }

    }

}
