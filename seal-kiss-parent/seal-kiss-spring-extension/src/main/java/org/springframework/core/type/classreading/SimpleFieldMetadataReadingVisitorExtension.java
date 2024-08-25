/*
 * Copyright 2002-2021 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.asm.*;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.lang.Nullable;

/**
 * ASM field visitor that creates {@link SimpleFieldMetadataExtension}.
 *
 * @author seal
 */
final class SimpleFieldMetadataReadingVisitorExtension extends FieldVisitor {

	@Nullable
	private final ClassLoader classLoader;

	private final String declaringClassName;

	private final int access;

	private final String fieldName;

	private final String descriptor;

	private final List<MergedAnnotation<?>> annotations = new ArrayList<>(4);

	private final Consumer<SimpleFieldMetadataExtension> consumer;

	@Nullable
	private Source source;


	SimpleFieldMetadataReadingVisitorExtension(@Nullable ClassLoader classLoader, String declaringClassName,
											   int access, String fieldName, String descriptor, Consumer<SimpleFieldMetadataExtension> consumer) {

		super(SpringAsmInfo.ASM_VERSION);
		this.classLoader = classLoader;
		this.declaringClassName = declaringClassName;
		this.access = access;
		this.fieldName = fieldName;
		this.descriptor = descriptor;
		this.consumer = consumer;
	}


	@Override
	@Nullable
	public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
		return MergedAnnotationReadingVisitor.get(this.classLoader, getSource(),
				descriptor, visible, this.annotations::add);
	}

	@Override
	public void visitEnd() {
		String returnTypeName = Type.getType(this.descriptor).getClassName();
		MergedAnnotations annotations = MergedAnnotations.of(this.annotations);
		SimpleFieldMetadataExtension metadata = new SimpleFieldMetadataExtension(this.fieldName, this.access,
				this.declaringClassName, returnTypeName, getSource(), annotations);
		this.consumer.accept(metadata);
	}

	private Object getSource() {
		Source source = this.source;
		if (source == null) {
			source = new Source(this.declaringClassName, this.fieldName, this.descriptor);
			this.source = source;
		}
		return source;
	}


	/**
	 * {@link MergedAnnotation} source.
	 */
	static final class Source {

		private final String declaringClassName;

		private final String fieldName;

		private final String descriptor;

		@Nullable
		private String toStringValue;

		Source(String declaringClassName, String fieldName, String descriptor) {
			this.declaringClassName = declaringClassName;
			this.fieldName = fieldName;
			this.descriptor = descriptor;
		}

		@Override
		public int hashCode() {
			int result = 1;
			result = 31 * result + this.declaringClassName.hashCode();
			result = 31 * result + this.fieldName.hashCode();
			result = 31 * result + this.descriptor.hashCode();
			return result;
		}

		@Override
		public boolean equals(@Nullable Object other) {
			if (this == other) {
				return true;
			}
			if (other == null || getClass() != other.getClass()) {
				return false;
			}
			Source otherSource = (Source) other;
			return (this.declaringClassName.equals(otherSource.declaringClassName) &&
					this.fieldName.equals(otherSource.fieldName) && this.descriptor.equals(otherSource.descriptor));
		}

		@Override
		public String toString() {
			String value = this.toStringValue;
			if (value == null) {
				StringBuilder builder = new StringBuilder();
				builder.append(this.declaringClassName);
				builder.append('.');
				builder.append(this.fieldName);
				Type[] argumentTypes = Type.getArgumentTypes(this.descriptor);
				builder.append('(');
				for (int i = 0; i < argumentTypes.length; i++) {
					if (i != 0) {
						builder.append(',');
					}
					builder.append(argumentTypes[i].getClassName());
				}
				builder.append(')');
				value = builder.toString();
				this.toStringValue = value;
			}
			return value;
		}
	}

}
