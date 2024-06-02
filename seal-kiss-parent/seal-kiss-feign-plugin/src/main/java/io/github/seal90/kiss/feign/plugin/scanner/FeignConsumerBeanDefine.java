package io.github.seal90.kiss.feign.plugin.scanner;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

import java.lang.reflect.Field;

public class FeignConsumerBeanDefine extends GenericBeanDefinition implements AnnotatedBeanDefinition {

    private AnnotationMetadata annotationMetadata;

    public FeignConsumerBeanDefine(AnnotationMetadata annotationMetadata) {
        this.annotationMetadata = annotationMetadata;
    }

    @Override
    public AnnotationMetadata getMetadata() {
        return this.annotationMetadata;
    }

    @Override
    public MethodMetadata getFactoryMethodMetadata() {
        return null;
    }
}
