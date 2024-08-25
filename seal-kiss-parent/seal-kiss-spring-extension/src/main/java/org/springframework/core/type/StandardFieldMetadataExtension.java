/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.core.type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.core.annotation.RepeatableContainers;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

/**
 * {@link FieldMetadataExtension} implementation that uses standard reflection
 * to introspect a given {@code Field}.
 *
 * @author seal
 */
public class StandardFieldMetadataExtension implements FieldMetadataExtension {

	private final Field introspectedField;

	private final boolean nestedAnnotationsAsMap;

	private final MergedAnnotations mergedAnnotations;

	/**
	 * Create a new StandardFieldMetadata wrapper for the given Field,
	 * providing the option to return any nested annotations or annotation arrays in the
	 * form of {@link org.springframework.core.annotation.AnnotationAttributes} instead
	 * of actual {@link java.lang.annotation.Annotation} instances.
	 * @param introspectedField the Field to introspect
	 * @param nestedAnnotationsAsMap return nested annotations and annotation arrays as
	 * {@link org.springframework.core.annotation.AnnotationAttributes} for compatibility
	 * with ASM-based {@link AnnotationMetadata} implementations
	 * @since 3.1.1
	 */
	StandardFieldMetadataExtension(Field introspectedField, boolean nestedAnnotationsAsMap) {
		Assert.notNull(introspectedField, "Field must not be null");
		this.introspectedField = introspectedField;
		this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
		this.mergedAnnotations = MergedAnnotations.from(
				introspectedField, SearchStrategy.DIRECT, RepeatableContainers.none());
	}


	@Override
	public MergedAnnotations getAnnotations() {
		return this.mergedAnnotations;
	}

	/**
	 * Return the underlying field.
	 */
	public final Field getIntrospectedField() {
		return this.introspectedField;
	}

	@Override
	public String getFieldName() {
		return this.introspectedField.getName();
	}

	@Override
	public String getDeclaringClassName() {
		return this.introspectedField.getDeclaringClass().getName();
	}

	@Override
	public String getFieldTypeName() {
		return this.introspectedField.getType().getName();
	}

	@Override
	public boolean isStatic() {
		return Modifier.isStatic(this.introspectedField.getModifiers());
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(this.introspectedField.getModifiers());
	}

	private boolean isPrivate() {
		return Modifier.isPrivate(this.introspectedField.getModifiers());
	}

	@Override
	@Nullable
	public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		if (this.nestedAnnotationsAsMap) {
			return FieldMetadataExtension.super.getAnnotationAttributes(annotationName, classValuesAsString);
		}
		return AnnotatedElementUtils.getMergedAnnotationAttributes(this.introspectedField,
				annotationName, classValuesAsString, false);
	}

	@Override
	@Nullable
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		if (this.nestedAnnotationsAsMap) {
			return FieldMetadataExtension.super.getAllAnnotationAttributes(annotationName, classValuesAsString);
		}
		return AnnotatedElementUtils.getAllAnnotationAttributes(this.introspectedField,
				annotationName, classValuesAsString, false);
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof StandardFieldMetadataExtension that &&
				this.introspectedField.equals(that.introspectedField)));
	}

	@Override
	public int hashCode() {
		return this.introspectedField.hashCode();
	}

	@Override
	public String toString() {
		return this.introspectedField.toString();
	}

}
