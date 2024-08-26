package io.github.seal90.kiss.spring.cloud.extension.loadbalancer;

import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    @Bean
    @ConditionalOnClass(value = {Feign.class, HttpServletRequest.class})
    @ConditionalOnProperty(name = "io.github.seal90.spring.cloud.extension.feign.subset.header.add", havingValue = "true", matchIfMissing=true)
    public RequestInterceptor headerRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
                if(attributes instanceof ServletRequestAttributes servletRequestAttributes) {
                    HttpServletRequest request = servletRequestAttributes.getRequest();
                    String grayFlag = request.getHeader(subsetEnvRequestKey);
                    if(StringUtils.hasText(grayFlag)) {
                        template.header(subsetEnvRequestKey, grayFlag);
                    }
                }
            }
        };
    }
}
