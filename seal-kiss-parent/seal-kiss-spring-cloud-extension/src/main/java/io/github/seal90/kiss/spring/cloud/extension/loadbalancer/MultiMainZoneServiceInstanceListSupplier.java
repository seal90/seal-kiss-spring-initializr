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

    private final String subsetEnvRequestKey;

    public MultiMainZoneServiceInstanceListSupplier(ServiceInstanceListSupplier delegate, String mainEnv, String subsetEnvRequestKey) {
        super(delegate);
        this.mainEnv = mainEnv;
        this.subsetEnvRequestKey = subsetEnvRequestKey;
    }

    @Override
    public Flux<List<ServiceInstance>> get(Request request) {
        String subsetEnvFlag = null;
        Object context = request.getContext();
        if ((context instanceof RequestDataContext)) {
            HttpHeaders headers = ((RequestDataContext) context).getClientRequest().getHeaders();
            String headerGrayFlag = headers.getFirst(subsetEnvRequestKey);
            if(StringUtils.hasText(headerGrayFlag)) {
                subsetEnvFlag = headerGrayFlag;
            }
        }

        String finalSubsetEnvFlag = subsetEnvFlag;
        Flux<List<ServiceInstance>> instances = super.getDelegate().get(request);
        if(StringUtils.hasText(finalSubsetEnvFlag)) {
            return instances.map(instanceList -> instanceList.stream().filter(
                                    instance -> mainEnv.equals(instance.getMetadata().get(AppConstant.MAIN_ZONE))
                                            && finalSubsetEnvFlag.equals(instance.getMetadata().get(AppConstant.SUBSET_ZONE)))
                            .collect(Collectors.toList())).filter(instanceList -> !instanceList.isEmpty())
                    .switchIfEmpty(instances.map(instanceList -> instanceList.stream().filter(
                                    instance -> mainEnv.equals(instance.getMetadata().get(AppConstant.MAIN_ZONE))
                                            && mainEnv.equals(instance.getMetadata().get(AppConstant.SUBSET_ZONE)))
                            .collect(Collectors.toList())));
        }
        return instances.map(instanceList -> instanceList.stream().filter(
                        instance -> mainEnv.equals(instance.getMetadata().get(AppConstant.MAIN_ZONE))
                                && mainEnv.equals(instance.getMetadata().get(AppConstant.SUBSET_ZONE)))
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