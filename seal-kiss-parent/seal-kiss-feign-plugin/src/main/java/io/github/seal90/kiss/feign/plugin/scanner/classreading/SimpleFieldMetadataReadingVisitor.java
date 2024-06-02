package io.github.seal90.kiss.feign.plugin.scanner.classreading;

import org.springframework.asm.*;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SimpleFieldMetadataReadingVisitor extends FieldVisitor {

    @Nullable
    private final ClassLoader classLoader;

    private final String declaringClassName;

    private final int access;

    private final String fieldName;

    private final String descriptor;

    private final List<MergedAnnotation<?>> annotations = new ArrayList<>(4);

    private final Consumer<SimpleFieldMetadata> consumer;

    @Nullable
    private Source source;

    SimpleFieldMetadataReadingVisitor(@Nullable ClassLoader classLoader, String declaringClassName,
                                       int access, String fieldName, String descriptor, Consumer<SimpleFieldMetadata> consumer) {

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

        SimpleFieldMetadata metadata = new SimpleFieldMetadata(this.fieldName, this.access,
                this.declaringClassName,  returnTypeName, getSource(), annotations);
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

    /**
     * {@link MetadataReader} implementation based on an ASM
     * {@link ClassReader}.
     *
     * @author Juergen Hoeller
     * @author Costin Leau
     * @since 2.5
     */
    static final class SimpleMetadataReader implements MetadataReader {

        private static final int PARSING_OPTIONS =
                (ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES);

        private final Resource resource;

        private final AnnotationMetadata annotationMetadata;


        SimpleMetadataReader(Resource resource, @Nullable ClassLoader classLoader) throws IOException {
            SimpleAnnotationMetadataReadingVisitor visitor = new SimpleAnnotationMetadataReadingVisitor(classLoader);
            getClassReader(resource).accept(visitor, PARSING_OPTIONS);
            this.resource = resource;
            this.annotationMetadata = visitor.getMetadata();
        }

        private static ClassReader getClassReader(Resource resource) throws IOException {
            try (InputStream is = resource.getInputStream()) {
                try {
                    return new ClassReader(is);
                }
                catch (IllegalArgumentException ex) {
                    throw new RuntimeException("ASM ClassReader failed to parse class file - " +
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

    }
}
