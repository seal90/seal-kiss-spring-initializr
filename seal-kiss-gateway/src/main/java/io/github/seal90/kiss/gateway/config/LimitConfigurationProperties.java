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
@ConfigurationProperties("seal.kiss.path-limit")
public class LimitConfigurationProperties {

    private Map<String, LimitRule> routeRules;

    @Data
    public static class LimitRule {

        private Boolean allAllow;

        private List<PathRule> pathRules;

        // TODO 这个转换如何处理
        private Map<String, Boolean> pathAuth;

        public LimitRule() {
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
