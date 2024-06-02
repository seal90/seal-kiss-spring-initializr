package io.github.seal90.kiss.feign.plugin;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import feign.Contract;
import feign.Request;

import io.github.seal90.kiss.feign.plugin.scanner.ClassPathScanningFieldCandidateComponentProvider;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.openfeign.*;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Spencer Gibb
 * @author Jakub Narloch
 * @author Venil Noronha
 * @author Gang Li
 * @author Michal Domagala
 * @author Marcin Grzejszczak
 * @author Olga Maciaszek-Sharma
 * @author Jasbir Singh
 */
class FeignConsumersRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

	// patterned after Spring Integration IntegrationComponentScanRegistrar
	// and RibbonClientsConfigurationRegistrar

	private ResourceLoader resourceLoader;

	private Environment environment;

	FeignConsumersRegistrar() {
	}

	static void validateFallback(final Class clazz) {
		Assert.isTrue(!clazz.isInterface(), "Fallback class must implement the interface annotated by @FeignClient");
	}

	static void validateFallbackFactory(final Class clazz) {
		Assert.isTrue(!clazz.isInterface(), "Fallback factory must produce instances "
				+ "of fallback classes that implement the interface annotated by @FeignClient");
	}

	static String getName(String name) {
		if (!StringUtils.hasText(name)) {
			return "";
		}

		String host = null;
		try {
			String url;
			if (!name.startsWith("http://") && !name.startsWith("https://")) {
				url = "http://" + name;
			}
			else {
				url = name;
			}
			host = new URI(url).getHost();

		}
		catch (URISyntaxException ignored) {
		}
		Assert.state(host != null, "Service id not legal hostname (" + name + ")");
		return name;
	}

	static String getUrl(String url) {
		if (StringUtils.hasText(url) && !(url.startsWith("#{") && url.contains("}"))) {
			if (!url.contains("://")) {
				url = "http://" + url;
			}
			try {
				new URL(url);
			}
			catch (MalformedURLException e) {
				throw new IllegalArgumentException(url + " is malformed", e);
			}
		}
		return url;
	}

	static String getPath(String path) {
		if (StringUtils.hasText(path)) {
			path = path.trim();
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
		}
		return path;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
//		registerDefaultConfiguration(metadata, registry);
		registerFeignClients(metadata, registry);
	}

	private void registerDefaultConfiguration(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		Map<String, Object> defaultAttrs = metadata.getAnnotationAttributes(EnableFeignConsumers.class.getName(), true);

		if (defaultAttrs != null && defaultAttrs.containsKey("defaultConfiguration")) {
			String name;
			if (metadata.hasEnclosingClass()) {
				name = "default." + metadata.getEnclosingClassName();
			}
			else {
				name = "default." + metadata.getClassName();
			}
			registerClientConfiguration(registry, name, "default", defaultAttrs.get("defaultConfiguration"));
		}
	}

	public void registerFeignClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
		Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableFeignConsumers.class.getName());
		final Class<?>[] clients = attrs == null ? null : (Class<?>[]) attrs.get("clients");
		if (clients == null || clients.length == 0) {
			ClassPathScanningFieldCandidateComponentProvider scanner = getScanner();
			scanner.setResourceLoader(this.resourceLoader);
			scanner.addIncludeFilter(new AnnotationTypeFilter(Configuration.class));
			Set<String> basePackages = getBasePackages(metadata);
			for (String basePackage : basePackages) {
				candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
			}
		}
		else {
			for (Class<?> clazz : clients) {
				candidateComponents.add(new AnnotatedGenericBeanDefinition(clazz));
			}
		}

		for (BeanDefinition candidateComponent : candidateComponents) {
			if (candidateComponent instanceof AnnotatedBeanDefinition beanDefinition) {
				// verify annotated class is an interface
				AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
				Assert.isTrue(annotationMetadata.isInterface(), "@FeignConsumer can only be specified on an interface");

				Map<String, Object> attributes = annotationMetadata
						.getAnnotationAttributes(FeignConsumer.class.getCanonicalName());

				String name = getClientName(attributes);
				String className = annotationMetadata.getClassName();
				registerClientConfiguration(registry, name, className, attributes.get("configuration"));

				registerFeignClient(registry, annotationMetadata, attributes);
			}
		}
	}

	private void registerFeignClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata,
			Map<String, Object> attributes) {
		String className = annotationMetadata.getClassName();
		if (String.valueOf(false).equals(
				environment.getProperty("spring.cloud.openfeign.lazy-attributes-resolution", String.valueOf(false)))) {
			eagerlyRegisterFeignClientBeanDefinition(className, attributes, registry);
		}
		else {
			lazilyRegisterFeignClientBeanDefinition(className, attributes, registry);
		}
	}

	private void eagerlyRegisterFeignClientBeanDefinition(String className, Map<String, Object> attributes,
			BeanDefinitionRegistry registry) {
		validate(attributes);
		String path = "";
        try {
            Class<?> clazz = ClassUtils.forName(className, this.resourceLoader.getClassLoader());
			RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
			if(null != requestMapping) {
				String[] paths = requestMapping.value();
				if (!ObjectUtils.isEmpty(paths) && StringUtils.hasLength(paths[0])) {
					path = paths[0];
				}
			}
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(FeignClientFactoryBean.class);
		definition.addPropertyValue("url", getUrl(null, attributes));
		String consumerPath = getPath(null, attributes);
		if(StringUtils.hasText(consumerPath)) {
			path = consumerPath;
		}
//		definition.addPropertyValue("path", );
		definition.addPropertyValue("path", path);
		String name = getName(attributes);
		definition.addPropertyValue("name", name);
		String contextId = getContextId(null, attributes);
		definition.addPropertyValue("contextId", contextId);
		definition.addPropertyValue("type", className);
		definition.addPropertyValue("dismiss404", Boolean.parseBoolean(String.valueOf(attributes.get("dismiss404"))));
		Object fallback = attributes.get("fallback");
		if (fallback != null) {
			definition.addPropertyValue("fallback",
					(fallback instanceof Class ? fallback : ClassUtils.resolveClassName(fallback.toString(), null)));
		}
		Object fallbackFactory = attributes.get("fallbackFactory");
		if (fallbackFactory != null) {
			definition.addPropertyValue("fallbackFactory", fallbackFactory instanceof Class ? fallbackFactory
					: ClassUtils.resolveClassName(fallbackFactory.toString(), null));
		}
		definition.addPropertyValue("fallbackFactory", attributes.get("fallbackFactory"));
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		definition.addPropertyValue("refreshableClient", isClientRefreshEnabled());
		String[] qualifiers = getQualifiers(attributes);
		if (ObjectUtils.isEmpty(qualifiers)) {
			qualifiers = new String[] { contextId + "FeignConsumer" };
		}
		// This is done so that there's a way to retrieve qualifiers while generating AOT
		// code
		definition.addPropertyValue("qualifiers", qualifiers);
		AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
		Class<?> type = ClassUtils.resolveClassName(className, null);
		beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, type);
		// has a default, won't be null
		boolean primary = (Boolean) attributes.get("primary");
		beanDefinition.setPrimary(primary);
		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, qualifiers);
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
		registerRefreshableBeanDefinition(registry, contextId, Request.Options.class, OptionsFactoryBean.class);
		registerRefreshableBeanDefinition(registry, contextId, RefreshableUrl.class, RefreshableUrlFactoryBean.class);
	}

	private void lazilyRegisterFeignClientBeanDefinition(String className, Map<String, Object> attributes,
			BeanDefinitionRegistry registry) {
		String path = "";
		ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory
				? (ConfigurableBeanFactory) registry : null;
		Class clazz = ClassUtils.resolveClassName(className, null);
		RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
		if(null != requestMapping) {
			String[] paths = requestMapping.value();
			if (!ObjectUtils.isEmpty(paths) && StringUtils.hasLength(paths[0])) {
				path = paths[0];
			}
		}
		String contextId = getContextId(beanFactory, attributes);
		String name = getName(attributes);
		FeignClientFactoryBean factoryBean = new FeignClientFactoryBean();
		factoryBean.setBeanFactory(beanFactory);
		factoryBean.setName(name);
		factoryBean.setContextId(contextId);
		factoryBean.setType(clazz);
		factoryBean.setRefreshableClient(isClientRefreshEnabled());
		String consumerPath = getPath(beanFactory, attributes);
		if(StringUtils.hasText(consumerPath)) {
			path = consumerPath;
		}
		String finalPath = path;
		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(clazz, () -> {
			factoryBean.setUrl(getUrl(beanFactory, attributes));
			factoryBean.setPath(finalPath);
			factoryBean.setDismiss404(Boolean.parseBoolean(String.valueOf(attributes.get("dismiss404"))));
			Object fallback = attributes.get("fallback");
			if (fallback != null) {
				factoryBean.setFallback(fallback instanceof Class ? (Class<?>) fallback
						: ClassUtils.resolveClassName(fallback.toString(), null));
			}
			Object fallbackFactory = attributes.get("fallbackFactory");
			if (fallbackFactory != null) {
				factoryBean.setFallbackFactory(fallbackFactory instanceof Class ? (Class<?>) fallbackFactory
						: ClassUtils.resolveClassName(fallbackFactory.toString(), null));
			}
			return factoryBean.getObject();
		});
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		definition.setLazyInit(true);
		validate(attributes);

		AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
		beanDefinition.setAttribute("feignClientsRegistrarFactoryBean", factoryBean);

		// has a default, won't be null
		boolean primary = (Boolean) attributes.get("primary");

		beanDefinition.setPrimary(primary);

		String[] qualifiers = getQualifiers(attributes);
		if (ObjectUtils.isEmpty(qualifiers)) {
			qualifiers = new String[] { contextId + "FeignConsumer" };
		}

		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, qualifiers);
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

		registerRefreshableBeanDefinition(registry, contextId, Request.Options.class, OptionsFactoryBean.class);
		registerRefreshableBeanDefinition(registry, contextId, RefreshableUrl.class, RefreshableUrlFactoryBean.class);
	}

	private void validate(Map<String, Object> attributes) {
		AnnotationAttributes annotation = AnnotationAttributes.fromMap(attributes);
		// This blows up if an aliased property is overspecified
		// FIXME annotation.getAliasedString("name", FeignClient.class, null);
		validateFallback(annotation.getClass("fallback"));
		validateFallbackFactory(annotation.getClass("fallbackFactory"));
	}

	/* for testing */ String getName(Map<String, Object> attributes) {
		return getName(null, attributes);
	}

	String getName(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
		String name = (String) attributes.get("serviceId");
		if (!StringUtils.hasText(name)) {
			name = (String) attributes.get("name");
		}
		if (!StringUtils.hasText(name)) {
			name = (String) attributes.get("value");
		}
		name = resolve(beanFactory, name);
		return getName(name);
	}

	private String getContextId(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
		String contextId = (String) attributes.get("contextId");
		if (!StringUtils.hasText(contextId)) {
			return getName(attributes);
		}

		contextId = resolve(beanFactory, contextId);
		return getName(contextId);
	}

	private String resolve(ConfigurableBeanFactory beanFactory, String value) {
		if (StringUtils.hasText(value)) {
			if (beanFactory == null) {
				return this.environment.resolvePlaceholders(value);
			}
			BeanExpressionResolver resolver = beanFactory.getBeanExpressionResolver();
			String resolved = beanFactory.resolveEmbeddedValue(value);
			if (resolver == null) {
				return resolved;
			}
			Object evaluateValue = resolver.evaluate(resolved, new BeanExpressionContext(beanFactory, null));
			if (evaluateValue != null) {
				return String.valueOf(evaluateValue);
			}
			return null;
		}
		return value;
	}

	private String getUrl(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
		String url = resolve(beanFactory, (String) attributes.get("url"));
		return getUrl(url);
	}

	private String getPath(ConfigurableBeanFactory beanFactory, Map<String, Object> attributes) {
		String path = resolve(beanFactory, (String) attributes.get("path"));
		return getPath(path);
	}

	protected ClassPathScanningFieldCandidateComponentProvider getScanner() {
		return new ClassPathScanningFieldCandidateComponentProvider(false, this.environment) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				boolean isCandidate = false;
				if (beanDefinition.getMetadata().isIndependent()) {
					if (!beanDefinition.getMetadata().isAnnotation()) {
						isCandidate = true;
					}
				}
				return isCandidate;
			}
		};
	}

	protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
		Map<String, Object> attributes = importingClassMetadata
				.getAnnotationAttributes(EnableFeignConsumers.class.getCanonicalName());

		Set<String> basePackages = new HashSet<>();
		for (String pkg : (String[]) attributes.get("value")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (String pkg : (String[]) attributes.get("basePackages")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}

		if (basePackages.isEmpty()) {
			basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}
		return basePackages;
	}

	private String getQualifier(Map<String, Object> client) {
		if (client == null) {
			return null;
		}
		String qualifier = (String) client.get("qualifier");
		if (StringUtils.hasText(qualifier)) {
			return qualifier;
		}
		return null;
	}

	private String[] getQualifiers(Map<String, Object> client) {
		if (client == null) {
			return null;
		}
		List<String> qualifierList = new ArrayList<>(Arrays.asList((String[]) client.get("qualifiers")));
		qualifierList.removeIf(qualifier -> !StringUtils.hasText(qualifier));
		if (qualifierList.isEmpty() && getQualifier(client) != null) {
			qualifierList = Collections.singletonList(getQualifier(client));
		}
		return !qualifierList.isEmpty() ? qualifierList.toArray(new String[0]) : null;
	}

	private String getClientName(Map<String, Object> client) {
		if (client == null) {
			return null;
		}
		String value = (String) client.get("contextId");
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("value");
		}
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("name");
		}
		if (!StringUtils.hasText(value)) {
			value = (String) client.get("serviceId");
		}
		if (StringUtils.hasText(value)) {
			return value;
		}

		throw new IllegalStateException(
				"Either 'name' or 'value' must be provided in @" + FeignConsumer.class.getSimpleName());
	}

	private void registerClientConfiguration(BeanDefinitionRegistry registry, Object name, Object className,
			Object configuration) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(FeignConsumerSpecification.class);
		builder.addConstructorArgValue(name);
		builder.addConstructorArgValue(className);
		builder.addConstructorArgValue(configuration);
		registry.registerBeanDefinition(name + "." + FeignConsumerSpecification.class.getSimpleName(),
				builder.getBeanDefinition());
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * This method registers beans definition with refreshScope.
	 * @param registry spring bean definition registry
	 * @param contextId name of feign client
	 * @param beanType type of bean
	 * @param factoryBeanType points to a relevant bean factory
	 */
	private void registerRefreshableBeanDefinition(BeanDefinitionRegistry registry, String contextId, Class<?> beanType,
			Class<?> factoryBeanType) {
		if (isClientRefreshEnabled()) {
			String beanName = beanType.getCanonicalName() + "-" + contextId;
			BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(factoryBeanType);
			definitionBuilder.setScope("refresh");
			definitionBuilder.addPropertyValue("contextId", contextId);
			BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(definitionBuilder.getBeanDefinition(),
					beanName);
			definitionHolder = ScopedProxyUtils.createScopedProxy(definitionHolder, registry, true);
			BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
		}
	}

	private boolean isClientRefreshEnabled() {
		return environment.getProperty("spring.cloud.openfeign.client.refresh-enabled", Boolean.class, false);
	}

}
