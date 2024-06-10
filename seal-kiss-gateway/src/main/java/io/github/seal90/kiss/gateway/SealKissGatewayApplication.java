package io.github.seal90.kiss.gateway;

import io.github.seal90.kiss.gateway.loadbalancer.GrayServiceInstanceListSupplierConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

@EnableDiscoveryClient
@SpringBootApplication
@LoadBalancerClients(defaultConfiguration = GrayServiceInstanceListSupplierConfiguration.class)
public class SealKissGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SealKissGatewayApplication.class, args);
	}

}
