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

package org.springframework.core.type.classreading;

import org.springframework.core.type.FieldMetadataExtension;
import org.springframework.asm.Opcodes;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;

/**
 * {@link FieldMetadataExtension} created from a {@link SimpleFieldMetadataReadingVisitorExtension}.
 *
 * @author seal
 */
final class SimpleFieldMetadataExtension implements FieldMetadataExtension {

	private final String fieldName;

	private final int access;

	private final String declaringClassName;

	private final String fieldTypeName;

	// The source implements equals(), hashCode(), and toString() for the underlying field.
	private final Object source;

	private final MergedAnnotations annotations;


	SimpleFieldMetadataExtension(String fieldName, int access, String declaringClassName,
								 String fieldTypeName, Object source, MergedAnnotations annotations) {

		this.fieldName = fieldName;
		this.access = access;
		this.declaringClassName = declaringClassName;
		this.fieldTypeName = fieldTypeName;
		this.source = source;
		this.annotations = annotations;
	}


	@Override
	public String getFieldName() {
		return this.fieldName;
	}

	@Override
	public String getDeclaringClassName() {
		return this.declaringClassName;
	}

	@Override
	public String getFieldTypeName() {
		return this.fieldTypeName;
	}

	@Override
	public boolean isStatic() {
		return (this.access & Opcodes.ACC_STATIC) != 0;
	}

	@Override
	public boolean isFinal() {
		return (this.access & Opcodes.ACC_FINAL) != 0;
	}

	private boolean isPrivate() {
		return (this.access & Opcodes.ACC_PRIVATE) != 0;
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return this.annotations;
	}


	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof SimpleFieldMetadataExtension that && this.source.equals(that.source)));
	}

	@Override
	public int hashCode() {
		return this.source.hashCode();
	}

	@Override
	public String toString() {
		return this.source.toString();
	}

}
