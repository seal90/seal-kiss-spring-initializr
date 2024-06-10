package io.github.seal90.kiss.gateway.config;

import io.github.seal90.kiss.gateway.filter.GrayCalculateFilter;
import io.github.seal90.kiss.gateway.filter.GrayPathFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayConfig {

    @Bean
    public GrayCalculateFilter grayCalculateFilter(GrayConfigurationProperties grayConfigurationProperties) {
        return new GrayCalculateFilter(grayConfigurationProperties);
    }

    @Bean
    public GrayPathFilter grayPathFilter() {
        return new GrayPathFilter();
    }
}
