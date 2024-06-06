package io.github.seal90.kiss.gateway.loadbalancer;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.loadbalancer.core.DiscoveryClientServiceInstanceListSupplier;
import org.springframework.core.env.Environment;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

public class SealDiscoveryClientServiceInstanceListSupplier extends DiscoveryClientServiceInstanceListSupplier {
    public SealDiscoveryClientServiceInstanceListSupplier(DiscoveryClient delegate, Environment environment) {
        super(delegate, environment);
    }

    public SealDiscoveryClientServiceInstanceListSupplier(ReactiveDiscoveryClient delegate, Environment environment) {
        super(delegate, environment);
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        Flux<List<ServiceInstance>> instances = super.get();
        return instances;
//        return instances.filter(instanceList -> !instanceList.isEmpty()).switchIfEmpty(instances);
//        return instances.map(instanceList ->
//                instanceList.stream().filter(
//                        instance ->
//                                "b".equals(instance.getMetadata().get("a")
//                        ))
//                        .collect(Collectors.toList())).filter(instanceList -> !instanceList.isEmpty()).switchIfEmpty(instances);
    }
}
