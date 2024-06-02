package com.github.seal90.kiss.multi;

import io.github.seal90.kiss.core.config.ExceptionAdviceConfig;
import io.github.seal90.kiss.core.config.ExceptionGlobalConfig;
import io.github.seal90.kiss.core.config.LogConfig;
import io.github.seal90.kiss.feign.plugin.EnableFeignConsumers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;


@EnableAspectJAutoProxy
@Import({ExceptionAdviceConfig.class, ExceptionGlobalConfig.class, LogConfig.class})
@SpringBootApplication
@EnableFeignClients
@EnableFeignConsumers
public class StartApplication {

	public static void main(String[] args) {
		SpringApplication.run(StartApplication.class, args);
	}

}
