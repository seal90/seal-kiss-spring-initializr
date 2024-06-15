package io.github.seal90.kiss.gateway.config;

import io.github.seal90.kiss.gateway.auth.AuthCacheAuthCheck;
import io.github.seal90.kiss.gateway.auth.AuthCheck;
import io.github.seal90.kiss.gateway.filter.GrayCalculateFilter;
import io.github.seal90.kiss.gateway.filter.GrayPathFilter;
import io.github.seal90.kiss.gateway.filter.AuthLimitPathFilter;
import io.github.seal90.kiss.gateway.filter.RateLimitPathFilter;
import io.github.seal90.kiss.gateway.login.service.LoginService;
import io.github.seal90.kiss.gateway.login.service.impl.AllAllowLoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayConfig {

    @Bean
    public RateLimitPathFilter rateLimitPathFilter(RateLimitPathConfigurationProperties rateLimitPathConfigurationProperties) {
        return new RateLimitPathFilter(rateLimitPathConfigurationProperties);
    }

    @Bean
    public AuthCheck allowAllLimitAuth() {
        return new AuthCacheAuthCheck();
    }

    @Bean
    public LoginService allAllowLoginService() {
        return new AllAllowLoginService();
    }

    @Bean
    public AuthLimitPathFilter limitPathFilter(AuthLimitConfigurationProperties limitConfigurationProperties, AuthCheck limitAuth) {
        return new AuthLimitPathFilter(limitConfigurationProperties, limitAuth);
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
