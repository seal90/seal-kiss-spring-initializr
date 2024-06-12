package io.github.seal90.kiss.gateway.config;

import io.github.seal90.kiss.gateway.auth.AllowAllLimitAuth;
import io.github.seal90.kiss.gateway.auth.LimitAuth;
import io.github.seal90.kiss.gateway.filter.GrayCalculateFilter;
import io.github.seal90.kiss.gateway.filter.GrayPathFilter;
import io.github.seal90.kiss.gateway.filter.LimitPathFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayConfig {

    @Bean
    public LimitAuth allowAllLimitAuth() {
        return new AllowAllLimitAuth();
    }

    @Bean
    public LimitPathFilter limitPathFilter(LimitConfigurationProperties limitConfigurationProperties, LimitAuth limitAuth) {
        return new LimitPathFilter(limitConfigurationProperties, limitAuth);
    }

    @Bean
    public GrayCalculateFilter grayCalculateFilter(GrayConfigurationProperties grayConfigurationProperties) {
        return new GrayCalculateFilter(grayConfigurationProperties);
    }

    @Bean
    public GrayPathFilter grayPathFilter() {
        return new GrayPathFilter();
    }

}
