package io.github.seal90.kiss.spring.cloud.extension.loadbalancer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

public class MainZoneServiceInstanceListSupplierConfiguration {

    @Value("${seal.kiss.env.main:DAILY}")
    private String mainEnv;

    @Value("${seal.kiss.gray.subsetEnvRequestKey:SUBSET_ENV}")
    private String subsetEnvRequestKey;

    @Bean
    public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
            ConfigurableApplicationContext context) {
        ServiceInstanceListSupplier delegate = ServiceInstanceListSupplier.builder().withDiscoveryClient().withCaching().build(context);
        return new MainZoneServiceInstanceListSupplier(delegate, mainEnv, subsetEnvRequestKey);
    }

}
