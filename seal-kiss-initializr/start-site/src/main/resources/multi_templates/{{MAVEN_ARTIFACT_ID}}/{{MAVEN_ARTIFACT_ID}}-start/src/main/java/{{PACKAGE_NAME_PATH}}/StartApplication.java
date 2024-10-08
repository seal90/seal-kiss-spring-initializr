package {{MAVEN_PACKAGE_NAME}};

import io.github.seal90.kiss.core.config.ExceptionAdviceConfig;
import io.github.seal90.kiss.core.config.ExceptionGlobalConfig;
import io.github.seal90.kiss.core.config.LogConfig;
import io.github.seal90.kiss.spring.cloud.extension.feign.EnableFeignConsumers;
import io.github.seal90.kiss.spring.cloud.extension.loadbalancer.MultiMainZoneServiceInstanceListSupplierConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.*;

@EnableAspectJAutoProxy
@Import({ExceptionAdviceConfig.class, ExceptionGlobalConfig.class, LogConfig.class})
@SpringBootApplication
@EnableFeignClients
@EnableFeignConsumers
@EnableDiscoveryClient
@LoadBalancerClients(defaultConfiguration = MultiMainZoneServiceInstanceListSupplierConfiguration.class)
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}

}
