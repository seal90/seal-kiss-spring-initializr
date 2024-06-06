/*
 * Copyright 2013-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.seal90.kiss.gateway.loadbalancer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cloud.loadbalancer.core.*;
import reactor.core.publisher.Mono;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.loadbalancer.cache.LoadBalancerCacheManager;
import org.springframework.cloud.loadbalancer.config.LoadBalancerZoneConfig;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertyResolver;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A Builder for creating a {@link ServiceInstanceListSupplier} hierarchy to be used in
 * {@link ReactorLoadBalancer} configuration.
 *
 * @author Spencer Gibb
 * @author Olga Maciaszek-Sharma
 * @author Zhiguo Chen
 * @author Sabyasachi Bhattacharya
 * @author Zhuozhi Ji
 */
public final class SealServiceInstanceListSupplierBuilder {

	private static final Log LOG = LogFactory.getLog(SealServiceInstanceListSupplierBuilder.class);

	private Creator baseCreator;

	private final List<DelegateCreator> creators = new ArrayList<>();

	public SealServiceInstanceListSupplierBuilder() {
	}

	/**
	 * Sets a blocking {@link DiscoveryClient}-based
	 * {@link DiscoveryClientServiceInstanceListSupplier} as a base
	 * {@link ServiceInstanceListSupplier} in the hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withBlockingDiscoveryClient() {
		if (baseCreator != null && LOG.isWarnEnabled()) {
			LOG.warn("Overriding a previously set baseCreator with a blocking DiscoveryClient baseCreator.");
		}
		this.baseCreator = context -> {
			DiscoveryClient discoveryClient = context.getBean(DiscoveryClient.class);

			return new SealDiscoveryClientServiceInstanceListSupplier(discoveryClient, context.getEnvironment());
		};
		return this;
	}

	/**
	 * Sets a {@link ReactiveDiscoveryClient}-based
	 * {@link DiscoveryClientServiceInstanceListSupplier} as a base
	 * {@link ServiceInstanceListSupplier} in the hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withDiscoveryClient() {
		if (baseCreator != null && LOG.isWarnEnabled()) {
			LOG.warn("Overriding a previously set baseCreator with a ReactiveDiscoveryClient baseCreator.");
		}
		this.baseCreator = context -> {
			ReactiveDiscoveryClient discoveryClient = context.getBean(ReactiveDiscoveryClient.class);

			return new SealDiscoveryClientServiceInstanceListSupplier(discoveryClient, context.getEnvironment());
		};
		return this;
	}

	/**
	 * Sets a user-provided {@link ServiceInstanceListSupplier} as a base
	 * {@link ServiceInstanceListSupplier} in the hierarchy.
	 * @param supplier a user-provided {@link ServiceInstanceListSupplier} instance
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withBase(ServiceInstanceListSupplier supplier) {
		this.baseCreator = context -> supplier;
		return this;
	}

	/**
	 * Adds a {@link WeightedServiceInstanceListSupplier} to the
	 * {@link ServiceInstanceListSupplier} hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withWeighted() {
		DelegateCreator creator = (context, delegate) -> {
			ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory = context
					.getBean(LoadBalancerClientFactory.class);
			return new WeightedServiceInstanceListSupplier(delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link WeightedServiceInstanceListSupplier} that uses user-provided
	 * {@link WeightFunction} instance to the {@link ServiceInstanceListSupplier}
	 * hierarchy.
	 * @param weightFunction a user-provided {@link WeightFunction} instance
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withWeighted(WeightFunction weightFunction) {
		DelegateCreator creator = (context, delegate) -> {
			ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory = context
					.getBean(LoadBalancerClientFactory.class);
			return new WeightedServiceInstanceListSupplier(delegate, weightFunction, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link HealthCheckServiceInstanceListSupplier} to the
	 * {@link ServiceInstanceListSupplier} hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withHealthChecks() {
		DelegateCreator creator = (context, delegate) -> {
			ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory = context
					.getBean(LoadBalancerClientFactory.class);
			WebClient.Builder webClient = context.getBean(WebClient.Builder.class);
			return healthCheckServiceInstanceListSupplier(webClient.build(), delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link HealthCheckServiceInstanceListSupplier} that uses user-provided
	 * {@link WebClient} instance to the {@link ServiceInstanceListSupplier} hierarchy.
	 * @param webClient a user-provided {@link WebClient} instance
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withHealthChecks(WebClient webClient) {
		DelegateCreator creator = (context, delegate) -> {
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			return healthCheckServiceInstanceListSupplier(webClient, delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link SameInstancePreferenceServiceInstanceListSupplier} to the
	 * {@link ServiceInstanceListSupplier} hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withSameInstancePreference() {
		DelegateCreator creator = (context, delegate) -> {
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			return new SameInstancePreferenceServiceInstanceListSupplier(delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link HealthCheckServiceInstanceListSupplier} that uses user-provided
	 * {@link RestTemplate} instance to the {@link ServiceInstanceListSupplier} hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withBlockingHealthChecks() {
		DelegateCreator creator = (context, delegate) -> {
			RestTemplate restTemplate = context.getBean(RestTemplate.class);
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			return blockingHealthCheckServiceInstanceListSupplier(restTemplate, delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link HealthCheckServiceInstanceListSupplier} that uses user-provided
	 * {@link RestClient} instance to the {@link ServiceInstanceListSupplier} hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withBlockingRestClientHealthChecks() {
		DelegateCreator creator = (context, delegate) -> {
			RestClient restClient = context.getBean(RestClient.class);
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			return blockingHealthCheckServiceInstanceListSupplier(restClient, delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link HealthCheckServiceInstanceListSupplier} that uses user-provided
	 * {@link RestTemplate} instance to the {@link ServiceInstanceListSupplier} hierarchy.
	 * @param restTemplate a user-provided {@link RestTemplate} instance
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withBlockingHealthChecks(RestTemplate restTemplate) {
		DelegateCreator creator = (context, delegate) -> {
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			return blockingHealthCheckServiceInstanceListSupplier(restTemplate, delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link HealthCheckServiceInstanceListSupplier} that uses user-provided
	 * {@link RestClient} instance to the {@link ServiceInstanceListSupplier} hierarchy.
	 * @param restClient a user-provided {@link RestClient} instance
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withBlockingHealthChecks(RestClient restClient) {
		DelegateCreator creator = (context, delegate) -> {
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			return blockingHealthCheckServiceInstanceListSupplier(restClient, delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link ZonePreferenceServiceInstanceListSupplier} to the
	 * {@link ServiceInstanceListSupplier} hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withZonePreference() {
		DelegateCreator creator = (context, delegate) -> {
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			LoadBalancerZoneConfig zoneConfig = context.getBean(LoadBalancerZoneConfig.class);
			return new ZonePreferenceServiceInstanceListSupplier(delegate, zoneConfig, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link ZonePreferenceServiceInstanceListSupplier} to the
	 * {@link ServiceInstanceListSupplier} hierarchy.
	 * @param zoneName desired zone for zone preference
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withZonePreference(String zoneName) {
		DelegateCreator creator = (context, delegate) -> {
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			LoadBalancerZoneConfig zoneConfig = new LoadBalancerZoneConfig(zoneName);
			return new ZonePreferenceServiceInstanceListSupplier(delegate, zoneConfig, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * Adds a {@link RequestBasedStickySessionServiceInstanceListSupplier} to the
	 * {@link ServiceInstanceListSupplier} hierarchy.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withRequestBasedStickySession() {
		DelegateCreator creator = (context, delegate) -> {
			LoadBalancerClientFactory loadBalancerClientFactory = context.getBean(LoadBalancerClientFactory.class);
			return new RequestBasedStickySessionServiceInstanceListSupplier(delegate, loadBalancerClientFactory);
		};
		this.creators.add(creator);
		return this;
	}

	/**
	 * If {@link LoadBalancerCacheManager} is available in the context, adds a
	 * {@link CachingServiceInstanceListSupplier} instance to the
	 * {@link ServiceInstanceListSupplier} hierarchy to provide a caching mechanism for
	 * service instances. Uses {@link ObjectProvider} to lazily resolve
	 * {@link LoadBalancerCacheManager}.
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder withCaching() {
		DelegateCreator creator = (context, delegate) -> {
			ObjectProvider<LoadBalancerCacheManager> cacheManagerProvider = context
					.getBeanProvider(LoadBalancerCacheManager.class);
			if (cacheManagerProvider.getIfAvailable() != null) {
				return new CachingServiceInstanceListSupplier(delegate, cacheManagerProvider.getIfAvailable());
			}
			if (LOG.isWarnEnabled()) {
				LOG.warn("LoadBalancerCacheManager not available, returning delegate without caching.");
			}
			return delegate;
		};
		creators.add(creator);
		return this;
	}

	public SealServiceInstanceListSupplierBuilder withRetryAwareness() {
		DelegateCreator creator = (context, delegate) -> new RetryAwareServiceInstanceListSupplier(delegate);
		creators.add(creator);
		return this;
	}

	public SealServiceInstanceListSupplierBuilder withHints() {
		DelegateCreator creator = (context, delegate) -> {
			LoadBalancerClientFactory factory = context.getBean(LoadBalancerClientFactory.class);
			return new HintBasedServiceInstanceListSupplier(delegate, factory);
		};
		creators.add(creator);
		return this;
	}

	public SealServiceInstanceListSupplierBuilder withSubset() {
		DelegateCreator creator = (context, delegate) -> {
			PropertyResolver resolver = context.getBean(PropertyResolver.class);
			LoadBalancerClientFactory factory = context.getBean(LoadBalancerClientFactory.class);
			return new SubsetServiceInstanceListSupplier(delegate, resolver, factory);
		};
		creators.add(creator);
		return this;
	}

	/**
	 * Support {@link SealServiceInstanceListSupplierBuilder} can be added to the expansion
	 * implementation of {@link ServiceInstanceListSupplier} by this method.
	 * @param delegateCreator a {@link DelegateCreator} object
	 * @return the {@link SealServiceInstanceListSupplierBuilder} object
	 */
	public SealServiceInstanceListSupplierBuilder with(DelegateCreator delegateCreator) {
		if (delegateCreator != null) {
			creators.add(delegateCreator);
		}
		return this;
	}

	/**
	 * Builds the {@link ServiceInstanceListSupplier} hierarchy.
	 * @param context application context
	 * @return a {@link ServiceInstanceListSupplier} instance on top of the delegate
	 * hierarchy
	 */
	public ServiceInstanceListSupplier build(ConfigurableApplicationContext context) {
		Assert.notNull(baseCreator, "A baseCreator must not be null");

		ServiceInstanceListSupplier supplier = baseCreator.apply(context);

		for (DelegateCreator creator : creators) {
			supplier = creator.apply(context, supplier);
		}

		return supplier;
	}

	private ServiceInstanceListSupplier healthCheckServiceInstanceListSupplier(WebClient webClient,
			ServiceInstanceListSupplier delegate,
			ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory) {
		return new HealthCheckServiceInstanceListSupplier(delegate, loadBalancerClientFactory,
				(serviceInstance, healthCheckPath) -> webClient.get()
						.uri(UriComponentsBuilder.fromUriString(getUri(serviceInstance, healthCheckPath)).build()
								.toUri())
						.exchange().flatMap(clientResponse -> clientResponse.releaseBody()
								.thenReturn(HttpStatus.OK.equals(clientResponse.statusCode()))));
	}

	private ServiceInstanceListSupplier blockingHealthCheckServiceInstanceListSupplier(RestTemplate restTemplate,
			ServiceInstanceListSupplier delegate,
			ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerClientFactory) {
		return new HealthCheckServiceInstanceListSupplier(delegate, loadBalancerClientFactory,
				(serviceInstance, healthCheckPath) -> Mono.defer(() -> {
					URI uri = UriComponentsBuilder.fromUriString(getUri(serviceInstance, healthCheckPath)).build()
							.toUri();
					try {
						return Mono
								.just(HttpStatus.OK.equals(restTemplate.getForEntity(uri, Void.class).getStatusCode()));
					}
					catch (Exception ignored) {
						return Mono.just(false);
					}
				}));
	}

	private ServiceInstanceListSupplier blockingHealthCheckServiceInstanceListSupplier(RestClient restClient,
			ServiceInstanceListSupplier delegate, LoadBalancerClientFactory loadBalancerClientFactory) {
		return new HealthCheckServiceInstanceListSupplier(delegate, loadBalancerClientFactory,
				(serviceInstance, healthCheckPath) -> Mono.defer(() -> {
					URI uri = UriComponentsBuilder.fromUriString(getUri(serviceInstance, healthCheckPath)).build()
							.toUri();
					try {
						return Mono.just(HttpStatus.OK
								.equals(restClient.get().uri(uri).retrieve().toBodilessEntity().getStatusCode()));
					}
					catch (Exception ignored) {
						return Mono.just(false);
					}
				}));
	}

	static String getUri(ServiceInstance serviceInstance, String healthCheckPath) {
		if (StringUtils.hasText(healthCheckPath)) {
			String path = healthCheckPath.startsWith("/") ? healthCheckPath : "/" + healthCheckPath;
			return serviceInstance.getUri().toString() + path;
		}
		return serviceInstance.getUri().toString();
	}

	/**
	 * Allows creating a {@link ServiceInstanceListSupplier} instance based on provided
	 * {@link ConfigurableApplicationContext}.
	 */
	public interface Creator extends Function<ConfigurableApplicationContext, ServiceInstanceListSupplier> {

	}

	/**
	 * Allows creating a {@link ServiceInstanceListSupplier} instance based on provided
	 * {@link ConfigurableApplicationContext} and another
	 * {@link ServiceInstanceListSupplier} instance that will be used as a delegate.
	 */
	public interface DelegateCreator extends
			BiFunction<ConfigurableApplicationContext, ServiceInstanceListSupplier, ServiceInstanceListSupplier> {

	}

}
