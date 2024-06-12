package io.github.seal90.kiss.spring.cloud.extension.loadbalancer;

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

public class GrayServiceInstanceListSupplier extends DelegatingServiceInstanceListSupplier {

    public static final String SEAL_GRAY_ENV_FLAG = "SEAL-GRAY-ENV";

    private final String runEnv;

    public GrayServiceInstanceListSupplier(ServiceInstanceListSupplier delegate, String runEnv) {
        super(delegate);
        this.runEnv = runEnv;
    }

    @Override
    public Flux<List<ServiceInstance>> get(Request request) {
        String reqGrayFlag = null;
        Object context = request.getContext();
        if ((context instanceof RequestDataContext)) {
            HttpHeaders headers = ((RequestDataContext) context).getClientRequest().getHeaders();
            String headerGrayFlag = headers.getFirst(SEAL_GRAY_ENV_FLAG);
            if(StringUtils.hasText(headerGrayFlag)) {
                reqGrayFlag = headerGrayFlag;
            }
        }
        String finalGrayFlag = reqGrayFlag;
        Flux<List<ServiceInstance>> instances = super.getDelegate().get(request);
        if(StringUtils.hasText(finalGrayFlag)) {
            return instances.map(instanceList ->
                            instanceList.stream().filter(
                                            instance -> finalGrayFlag.equals(instance.getMetadata().get(SEAL_GRAY_ENV_FLAG))
                                    )
                                    .collect(Collectors.toList())).filter(instanceList -> !instanceList.isEmpty())
                    .switchIfEmpty(instances.map(instanceList ->
                            instanceList.stream().filter(
                                            instance -> runEnv.equals(instance.getMetadata().get(SEAL_GRAY_ENV_FLAG))
                                    )
                                    .collect(Collectors.toList())));
        }
        return instances.map(instanceList ->
                instanceList.stream().filter(
                                instance -> runEnv.equals(instance.getMetadata().get(SEAL_GRAY_ENV_FLAG))
                        )
                        .collect(Collectors.toList()));
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return null;
    }
}
