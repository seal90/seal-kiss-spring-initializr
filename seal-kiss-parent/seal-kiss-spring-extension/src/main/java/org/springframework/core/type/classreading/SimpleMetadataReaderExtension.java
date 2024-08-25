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

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.asm.ClassReader;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.FieldMetadataExtension;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

/**
 * {@link MetadataReaderExtension} implementation based on an ASM
 * {@link org.springframework.asm.ClassReader}.
 *
 * @author Juergen Hoeller
 * @author Costin Leau
 * @since 2.5
 */
final class SimpleMetadataReaderExtension implements MetadataReaderExtension {

	private static final int PARSING_OPTIONS =
			(ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);

	private final Resource resource;

	private final AnnotationMetadata annotationMetadata;

	private final Set<MethodMetadata> declaredMethods;

	private final Set<FieldMetadataExtension> declaredFields;


	SimpleMetadataReaderExtension(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
		SimpleAnnotationMetadataReadingVisitorExtension visitor = new SimpleAnnotationMetadataReadingVisitorExtension(classLoader);
		getClassReader(resource).accept(visitor, PARSING_OPTIONS);
		this.resource = resource;
		this.annotationMetadata = visitor.getMetadata();
		this.declaredMethods = visitor.getDeclaredMethods();
		this.declaredFields = visitor.getDeclaredFields();
	}

	SimpleMetadataReaderExtension(Resource resource, @Nullable ClassLoader classLoader, MergedAnnotations mergedAnnotations) throws IOException {
		SimpleAnnotationMetadataReadingVisitorExtension visitor = new SimpleAnnotationMetadataReadingVisitorExtension(classLoader);
		getClassReader(resource).accept(visitor, PARSING_OPTIONS);
		SimpleAnnotationMetadataExtension metadata = visitor.getMetadata();
		MergedAnnotations originMergedAnnotations = metadata.getMergedAnnotations();
		List<Annotation> annotations = new ArrayList<>();
		annotations.addAll(originMergedAnnotations.stream().map(MergedAnnotation::synthesize).toList());
		annotations.addAll(mergedAnnotations.stream().map(MergedAnnotation::synthesize).toList());
		MergedAnnotations allMergedAnnotations = MergedAnnotations.from(annotations.toArray(new Annotation[]{}));
		SimpleAnnotationMetadataExtension newMetadata = new SimpleAnnotationMetadataExtension(metadata.getClassName(), metadata.getAccess(), metadata.getEnclosingClassName(),
				metadata.getSuperClassName(), metadata.isIndependentInnerClass(), Set.of(metadata.getInterfaceNames()),
				Set.of(metadata.getMemberClassNames()), metadata.getDeclaredMethods(), metadata.getDeclaredFields(), allMergedAnnotations);
		this.resource = resource;
		this.annotationMetadata = newMetadata;
		this.declaredMethods = visitor.getDeclaredMethods();
		this.declaredFields = visitor.getDeclaredFields();
	}

	private static ClassReader getClassReader(Resource resource) throws IOException {
		try (InputStream is = resource.getInputStream()) {
			try {
				return new ClassReader(is);
			}
			catch (IllegalArgumentException ex) {
				throw new ClassFormatException("ASM ClassReader failed to parse class file - " +
						"probably due to a new Java class file version that is not supported yet. " +
						"Consider compiling with a lower '-target' or upgrade your framework version. " +
						"Affected class: " + resource, ex);
			}
		}
	}


	@Override
	public Resource getResource() {
		return this.resource;
	}

	@Override
	public ClassMetadata getClassMetadata() {
		return this.annotationMetadata;
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata() {
		return this.annotationMetadata;
	}

	public Set<MethodMetadata> getDeclaredMethods() {
		return declaredMethods;
	}

	public Set<FieldMetadataExtension> getDeclaredFields() {
		return declaredFields;
	}
}
