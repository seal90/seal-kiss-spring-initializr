package io.github.seal90.kiss.feign.plugin;

import feign.Contract;
import feign.MethodMetadata;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.CollectionFormat;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * @author Spencer Gibb
 * @author Abhijit Sarkar
 * @author Halvdan Hoem Grelland
 * @author Aram Peres
 * @author Olga Maciaszek-Sharma
 * @author Aaron Whiteside
 * @author Artyom Romanenko
 * @author Darren Foong
 * @author Ram Anaswara
 * @author Sam Kruglov
 */
public class SealSpringMvcContractConfig {

	private static final Log LOG = LogFactory.getLog(SealSpringMvcContract.class);

	@Value("${seal.kiss.gray.subSetEnvRequestKey:SUB_SET_ENV}")
	private String subSetEnvRequestKey;

	@Autowired(required = false)
	private FeignClientProperties feignClientProperties;

	@Autowired(required = false)
	private List<AnnotatedParameterProcessor> parameterProcessors = new ArrayList<>();

	@Bean
	public RequestInterceptor headerRequestInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate template) {
				RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
				if(attributes instanceof ServletRequestAttributes servletRequestAttributes) {
					HttpServletRequest request = servletRequestAttributes.getRequest();
					String grayFlag = request.getHeader(subSetEnvRequestKey);
					if(StringUtils.hasText(grayFlag)) {
						template.header(subSetEnvRequestKey, grayFlag);
					}
				}
			}
		};
	}

	@Bean
	@ConditionalOnProperty(name = "io.github.seal90.feign.mvc.contract", havingValue = "true", matchIfMissing=true)
	public Contract feignContract(ConversionService feignConversionService) {
		boolean decodeSlash = feignClientProperties == null || feignClientProperties.isDecodeSlash();
		return new SealSpringMvcContract(parameterProcessors, feignConversionService, decodeSlash);
	}

	private static final class SealSpringMvcContract extends SpringMvcContract {
		public SealSpringMvcContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors,
								 ConversionService conversionService, boolean decodeSlash) {
			super(annotatedParameterProcessors, conversionService, decodeSlash);
		}

		@Override
		protected void processAnnotationOnClass(MethodMetadata data, Class<?> clz) {
			RequestMapping classAnnotation = findMergedAnnotation(clz, RequestMapping.class);
			FeignClient feignClassAnnotation = findMergedAnnotation(clz, FeignClient.class);
			if (classAnnotation != null && feignClassAnnotation != null) {
				LOG.error("Cannot process class: " + clz.getName()
						+ ". @RequestMapping annotation is not allowed on @FeignClient interfaces.");
				throw new IllegalArgumentException("@RequestMapping annotation not allowed on @FeignClient interfaces");
			}

			CollectionFormat collectionFormat = findMergedAnnotation(clz, CollectionFormat.class);
			if (collectionFormat != null) {
				data.template().collectionFormat(collectionFormat.value());
			}
		}
	}

}