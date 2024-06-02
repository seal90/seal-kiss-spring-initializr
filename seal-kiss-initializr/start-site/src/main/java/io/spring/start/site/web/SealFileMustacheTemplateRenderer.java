//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.spring.start.site.web;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

import io.spring.initializr.generator.io.template.TemplateRenderer;
import org.springframework.cache.Cache;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class SealFileMustacheTemplateRenderer implements TemplateRenderer {
    private final Mustache.Compiler mustache;
    private final Function<String, String> keyGenerator;
    private final Cache templateCache;

    public SealFileMustacheTemplateRenderer(String resourcePrefix, Cache templateCache) {
        String prefix = resourcePrefix.endsWith("/") ? resourcePrefix : resourcePrefix + "/";
        this.mustache = Mustache.compiler().withLoader(mustacheTemplateLoader(prefix)).escapeHTML(false);
        this.keyGenerator = (name) -> {
            return String.format("%s%s", prefix, name);
        };
        this.templateCache = templateCache;
    }

    public SealFileMustacheTemplateRenderer(String resourcePrefix) {
        this(resourcePrefix, (Cache)null);
    }

    private static Mustache.TemplateLoader mustacheTemplateLoader(String prefix) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        return (name) -> {
            String location = prefix + name;
            return new InputStreamReader(resourceLoader.getResource(location).getInputStream(), StandardCharsets.UTF_8);
        };
    }

    public String render(String templateName, Map<String, ?> model) {
        Template template = this.getTemplate(templateName);
        return template.execute(model);
    }

    private Template getTemplate(String name) {
        try {
            if (this.templateCache != null) {
                try {
                    return (Template)this.templateCache.get(this.keyGenerator.apply(name), () -> {
                        return this.loadTemplate(name);
                    });
                } catch (Cache.ValueRetrievalException var3) {
                    throw var3.getCause();
                }
            } else {
                return this.loadTemplate(name);
            }
        } catch (Throwable var4) {
            throw new IllegalStateException("Cannot load template " + name, var4);
        }
    }

    private Template loadTemplate(String name) throws Exception {
        Reader template = this.mustache.loader.getTemplate(name);
        return this.mustache.compile(template);
    }
}
