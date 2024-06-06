package io.github.seal90.kiss.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class SealKissGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SealKissGatewayApplication.class, args);
	}

}
