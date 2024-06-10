package io.github.seal90.kiss.spring.cloud.extension.loadbalancer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public class GrayServiceInstanceListSupplierConfiguration {

    @Value("${seal.kiss.env.run:DAILY}")
    private String runEnv;

    @Value("${seal.kiss.env.gray:}")
    private String grayEnv;

    @Bean
    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
            ConfigurableApplicationContext context) {
        ServiceInstanceListSupplier delegate = ServiceInstanceListSupplier.builder().withBlockingDiscoveryClient().build(context);
        return new GrayServiceInstanceListSupplier(delegate, runEnv, grayEnv);
    }
}
