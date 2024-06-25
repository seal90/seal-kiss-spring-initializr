package io.github.seal90.kiss.spring.cloud.extension.loadbalancer;

import io.github.seal90.kiss.core.constant.AppConstant;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.RequestDataContext;
import org.springframework.cloud.loadbalancer.core.DelegatingServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

public class MultiMainZoneServiceInstanceListSupplier  extends DelegatingServiceInstanceListSupplier {

    private final String mainEnv;

    private final String subSetEnvRequestKey;

    public MultiMainZoneServiceInstanceListSupplier(ServiceInstanceListSupplier delegate, String mainEnv, String subSetEnvRequestKey) {
        super(delegate);
        this.mainEnv = mainEnv;
        this.subSetEnvRequestKey = subSetEnvRequestKey;
    }

    @Override
    public Flux<List<ServiceInstance>> get(Request request) {
        String subSetEnvFlag = null;
        Object context = request.getContext();
        if ((context instanceof RequestDataContext)) {
            HttpHeaders headers = ((RequestDataContext) context).getClientRequest().getHeaders();
            String headerGrayFlag = headers.getFirst(subSetEnvRequestKey);
            if(StringUtils.hasText(headerGrayFlag)) {
                subSetEnvFlag = headerGrayFlag;
            }
        }

        String finalSubSetEnvFlag = subSetEnvFlag;
        Flux<List<ServiceInstance>> instances = super.getDelegate().get(request);
        if(StringUtils.hasText(finalSubSetEnvFlag)) {
            return instances.map(instanceList -> instanceList.stream().filter(
                                    instance -> mainEnv.equals(instance.getMetadata().get(AppConstant.MAIN_ZONE_ENV))
                                            && finalSubSetEnvFlag.equals(instance.getMetadata().get(AppConstant.SUB_SET_ZONE_ENV)))
                            .collect(Collectors.toList())).filter(instanceList -> !instanceList.isEmpty())
                    .switchIfEmpty(instances.map(instanceList -> instanceList.stream().filter(
                                    instance -> mainEnv.equals(instance.getMetadata().get(AppConstant.MAIN_ZONE_ENV)))
                            .collect(Collectors.toList())));
        }
        return instances.map(instanceList -> instanceList.stream().filter(
                        instance -> mainEnv.equals(instance.getMetadata().get(AppConstant.MAIN_ZONE_ENV)))
                .collect(Collectors.toList()));
    }

    /**
     * 这个方法不会被调用
     * @return
     */
    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.empty();
    }
}