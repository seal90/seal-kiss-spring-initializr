package io.github.seal90.kiss.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties("seal.kiss.auth-path-limit")
public class AuthLimitConfigurationProperties {

    private Map<String, RouteRule> routeRules;

    @Data
    public static class RouteRule {

        private Boolean allAllow;

        private List<PathRule> pathRules;

        private Map<String, Boolean> pathAuth;

        public RouteRule() {
            this.allAllow = false;
            this.pathRules = Collections.emptyList();
        }

        public Map<String, Boolean> getPathAuth() {

            if(null == this.pathAuth && !CollectionUtils.isEmpty(this.pathRules)) {
                this.pathAuth = this.pathRules.stream().collect(Collectors.toMap(pathRule -> pathRule.getMethod().name()+"_"+pathRule.getPath()
                        , PathRule::getNeedAuth, (key1, key2) -> key1));
            }
            return this.pathAuth;
        }

        public void setPathAuth(Map<String, Boolean> pathAuth) {
            // ignore any data
        }

    }

    @Data
    public static class PathRule {

        private HttpMethod method;

        private String path;

        private Boolean needAuth;

        public PathRule() {
            this.method = HttpMethod.POST;
            this.needAuth = true;
        }
    }
}
