package io.github.seal90.kiss.gateway.config;

import io.github.seal90.kiss.gateway.loadbalancer.SealDiscoveryClientServiceInstanceListSupplier;
import io.github.seal90.kiss.gateway.loadbalancer.SealServiceInstanceListSupplierBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ConditionalOnReactiveDiscoveryEnabled;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryClientRouteDefinitionLocator;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClientConfiguration;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.XForwardedHeadersTransformer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import io.github.seal90.kiss.gateway.filter.factory.StaticGrayGatewayFilterFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {
    
    @Bean
    public StaticGrayGatewayFilterFactory staticGrayGatewayFilterFactory() {
        return new StaticGrayGatewayFilterFactory();
    }

    // @Configuration(proxyBeanMethods = false)
	// @ConditionalOnProperty(value = "spring.cloud.discovery.reactive.enabled", matchIfMissing = true)
	// public static class ReactiveDiscoveryClientRouteDefinitionLocatorConfiguration {

	// 	@Bean
	// 	@ConditionalOnProperty(name = "spring.cloud.gateway.discovery.locator.enabled")
	// 	public DiscoveryClientRouteDefinitionLocator discoveryClientRouteDefinitionLocator(
	// 			ReactiveDiscoveryClient discoveryClient, DiscoveryLocatorProperties properties) {
	// 		return new DiscoveryClientRouteDefinitionLocator(discoveryClient, properties);
	// 	}

	// }

//    @Bean
//    public ServiceInstanceListSupplier sealDiscoveryClientServiceInstanceListSupplier(DiscoveryClient delegate, Environment environment) {
//        return new SealDiscoveryClientServiceInstanceListSupplier(delegate, environment);
//    }

//    @Configuration(proxyBeanMethods = false)
//    @ConditionalOnReactiveDiscoveryEnabled
//    @Order(-1)
    public static class ReactiveSupportConfiguration {

        @Bean
        @ConditionalOnBean(ReactiveDiscoveryClient.class)
        public ServiceInstanceListSupplier discoveryClientServiceInstanceListSupplier(
                ConfigurableApplicationContext context) {
            return new SealServiceInstanceListSupplierBuilder().withDiscoveryClient().withCaching().build(context);
        }

        @Bean
        @ConditionalOnBean(ReactiveDiscoveryClient.class)
        public ServiceInstanceListSupplier zonePreferenceDiscoveryClientServiceInstanceListSupplier(
                ConfigurableApplicationContext context) {
            return new SealServiceInstanceListSupplierBuilder().withDiscoveryClient().withCaching().withZonePreference()
                    .build(context);
        }

        @Bean
        @ConditionalOnBean(LoadBalancerClientFactory.class)
        public XForwardedHeadersTransformer xForwarderHeadersTransformer(LoadBalancerClientFactory clientFactory) {
            return new XForwardedHeadersTransformer(clientFactory);
        }

        @Bean
        @ConditionalOnBean({ ReactiveDiscoveryClient.class, WebClient.Builder.class })
        public ServiceInstanceListSupplier healthCheckDiscoveryClientServiceInstanceListSupplier(
                ConfigurableApplicationContext context) {
            return new SealServiceInstanceListSupplierBuilder().withDiscoveryClient().withHealthChecks().build(context);
        }

        @Bean
        @ConditionalOnBean(ReactiveDiscoveryClient.class)
        public ServiceInstanceListSupplier requestBasedStickySessionDiscoveryClientServiceInstanceListSupplier(
                ConfigurableApplicationContext context) {
            return new SealServiceInstanceListSupplierBuilder().withDiscoveryClient().withCaching()
                    .withRequestBasedStickySession().build(context);
        }

        @Bean
        @ConditionalOnBean(ReactiveDiscoveryClient.class)
        public ServiceInstanceListSupplier sameInstancePreferenceServiceInstanceListSupplier(
                ConfigurableApplicationContext context) {
            return new SealServiceInstanceListSupplierBuilder().withDiscoveryClient().withCaching()
                    .withSameInstancePreference().build(context);
        }

        @Bean
        @ConditionalOnBean(ReactiveDiscoveryClient.class)
        public ServiceInstanceListSupplier weightedServiceInstanceListSupplier(ConfigurableApplicationContext context) {
            return new SealServiceInstanceListSupplierBuilder().withDiscoveryClient().withCaching().withWeighted()
                    .build(context);
        }

        @Bean
        @ConditionalOnBean(ReactiveDiscoveryClient.class)
        public ServiceInstanceListSupplier subsetServiceInstanceListSupplier(ConfigurableApplicationContext context) {
            return new SealServiceInstanceListSupplierBuilder().withDiscoveryClient().withSubset().withCaching()
                    .build(context);
        }

    }
}
