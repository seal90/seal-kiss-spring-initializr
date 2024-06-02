package io.github.seal90.kiss.core.config;

import io.github.seal90.kiss.core.extension.ExtensionManager;
import io.github.seal90.kiss.core.extension.ExtensionManagerSpringImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class ExtensionConfig {

    @Bean
    @ConditionalOnMissingBean(ExtensionManager.class)
    public ExtensionManager extensionManager(){
        return new ExtensionManagerSpringImpl();
    }
}
