package io.github.seal90.kiss.spring.cloud.extension.loadbalancer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

public class MultiMainZoneServiceInstanceListSupplierConfiguration {

    @Value("${seal.kiss.env.main:DAILY}")
    private String mainEnv;

    @Value("${seal.kiss.gray.subSetEnvRequestKey:SUB_SET_ENV}")
    private String subSetEnvRequestKey;

    @Bean
    @ConditionalOnBean(ReactiveDiscoveryClient.class)
    @ConditionalOnMissingBean
    public ServiceInstanceListSupplier multiMainZoneServiceInstanceListSupplier(ConfigurableApplicationContext context) {
        ServiceInstanceListSupplier delegate = ServiceInstanceListSupplier.builder().withDiscoveryClient().withCaching().build(context);
        return new MultiMainZoneServiceInstanceListSupplier(delegate, mainEnv, subSetEnvRequestKey);
    }

    @Bean
    @ConditionalOnBean(DiscoveryClient.class)
    @ConditionalOnMissingBean
    public ServiceInstanceListSupplier blockingMultiMainZoneServiceInstanceListSupplier(
            ConfigurableApplicationContext context) {
        ServiceInstanceListSupplier delegate = ServiceInstanceListSupplier.builder().withBlockingDiscoveryClient().withCaching().build(context);
        return new MultiMainZoneServiceInstanceListSupplier(delegate, mainEnv, subSetEnvRequestKey);
    }

}
