package io.github.seal90.kiss.gateway;

import io.github.seal90.kiss.spring.cloud.extension.loadbalancer.MultiMainZoneServiceInstanceListSupplierConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import reactor.core.publisher.Hooks;

@EnableDiscoveryClient
@SpringBootApplication
@LoadBalancerClients(defaultConfiguration = MultiMainZoneServiceInstanceListSupplierConfiguration.class)
public class SealKissGatewayApplication {

	public static void main(String[] args) {
		Hooks.enableAutomaticContextPropagation();
		SpringApplication.run(SealKissGatewayApplication.class, args);
	}

}
