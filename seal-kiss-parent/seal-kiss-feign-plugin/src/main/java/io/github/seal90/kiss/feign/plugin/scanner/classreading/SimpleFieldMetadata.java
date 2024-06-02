package io.github.seal90.kiss.feign.plugin.scanner.classreading;

import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;


public class SimpleFieldMetadata implements AnnotatedTypeMetadata {

    private final String fieldName;

    private final int access;

    private final String declaringClassName;

    private final String fieldTypeName;

    // The source implements equals(), hashCode(), and toString() for the underlying method.
    private final Object source;

    private final MergedAnnotations annotations;


    public SimpleFieldMetadata(String fieldName, int access, String declaringClassName,
                         String fieldTypeName, Object source, MergedAnnotations annotations) {

        this.fieldName = fieldName;
        this.access = access;
        this.declaringClassName = declaringClassName;
        this.fieldTypeName = fieldTypeName;
        this.source = source;
        this.annotations = annotations;
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return annotations;
    }

    public String getFieldName() {
        return fieldName;
    }

    public int getAccess() {
        return access;
    }

    public String getDeclaringClassName() {
        return declaringClassName;
    }

    public String getFieldTypeName() {
        return fieldTypeName;
    }

    public Object getSource() {
        return source;
    }
}
