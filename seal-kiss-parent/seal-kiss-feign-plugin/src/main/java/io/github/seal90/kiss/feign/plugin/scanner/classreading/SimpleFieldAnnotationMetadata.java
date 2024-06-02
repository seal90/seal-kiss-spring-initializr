package io.github.seal90.kiss.feign.plugin.scanner.classreading;

import org.springframework.asm.Opcodes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleFieldAnnotationMetadata implements AnnotationMetadata {

    private final String className;

    private final int access;

    @Nullable
    private final String fieldClassName;

    @Nullable
    private final String superClassName;

    private final boolean independentInnerClass;

    private final Set<String> interfaceNames;

    private final Set<String> memberClassNames;

    private final Set<MethodMetadata> declaredMethods;

    private final MergedAnnotations mergedAnnotations;

    @Nullable
    private Set<String> annotationTypes;


    public SimpleFieldAnnotationMetadata(String className, int access, @Nullable String enclosingClassName,
                             @Nullable String superClassName, boolean independentInnerClass, Set<String> interfaceNames,
                             Set<String> memberClassNames, Set<MethodMetadata> declaredMethods, MergedAnnotations mergedAnnotations) {

        this.className = className;
        this.access = access;
        this.fieldClassName = enclosingClassName;
        this.superClassName = superClassName;
        this.independentInnerClass = independentInnerClass;
        this.interfaceNames = interfaceNames;
        this.memberClassNames = memberClassNames;
        this.declaredMethods = declaredMethods;
        this.mergedAnnotations = mergedAnnotations;
    }

    @Override
    public String getClassName() {
        return this.fieldClassName;
    }

    @Override
    public boolean isInterface() {
        return true;
    }

    @Override
    public boolean isAnnotation() {
        return (this.access & Opcodes.ACC_ANNOTATION) != 0;
    }

    @Override
    public boolean isAbstract() {
        return (this.access & Opcodes.ACC_ABSTRACT) != 0;
    }

    @Override
    public boolean isFinal() {
        return (this.access & Opcodes.ACC_FINAL) != 0;
    }

    @Override
    public boolean isIndependent() {
        return (this.fieldClassName == null || this.independentInnerClass);
    }

    @Override
    @Nullable
    public String getEnclosingClassName() {
        return this.fieldClassName;
    }

    @Override
    @Nullable
    public String getSuperClassName() {
        return this.superClassName;
    }

    @Override
    public String[] getInterfaceNames() {
        return StringUtils.toStringArray(this.interfaceNames);
    }

    @Override
    public String[] getMemberClassNames() {
        return StringUtils.toStringArray(this.memberClassNames);
    }

    @Override
    public MergedAnnotations getAnnotations() {
        return this.mergedAnnotations;
    }

    @Override
    public Set<String> getAnnotationTypes() {
        Set<String> annotationTypes = this.annotationTypes;
        if (annotationTypes == null) {
            annotationTypes = Collections.unmodifiableSet(
                    AnnotationMetadata.super.getAnnotationTypes());
            this.annotationTypes = annotationTypes;
        }
        return annotationTypes;
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        Set<MethodMetadata> result = new LinkedHashSet<>(4);
        for (MethodMetadata annotatedMethod : this.declaredMethods) {
            if (annotatedMethod.isAnnotated(annotationName)) {
                result.add(annotatedMethod);
            }
        }
        return Collections.unmodifiableSet(result);
    }

    @Override
    public Set<MethodMetadata> getDeclaredMethods() {
        return Collections.unmodifiableSet(this.declaredMethods);
    }


    @Override
    public boolean equals(@Nullable Object other) {
        return (this == other || (other instanceof SimpleFieldAnnotationMetadata that && this.className.equals(that.className)));
    }

    @Override
    public int hashCode() {
        return this.className.hashCode();
    }

    @Override
    public String toString() {
        return this.className;
    }

}
