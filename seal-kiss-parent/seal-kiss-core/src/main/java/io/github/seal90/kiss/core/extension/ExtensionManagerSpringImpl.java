package io.github.seal90.kiss.core.extension;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 扩展点管理实现类
 *
 */
@Slf4j
public class ExtensionManagerSpringImpl<T>
    implements ExtensionManager, ApplicationListener<ContextRefreshedEvent> {

    /** extensionMap */
    private Map<String, T> extensionMap = new HashMap<>(16);

    @Override
    public T getExtension(ExtensionTypeEnum type, String name) {
        return getExtension(type, name, null);
    }

    @Override
    public T getExtension(ExtensionTypeEnum type, String name, String action) {
        if (Objects.isNull(name) || Objects.isNull(type)) {
            log.info("name或者ExtensionTypeEnum为空，无法获取extension");
            return null;
        }
        return extensionMap.get(buildExtensionKey(type, name, action));
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Map<String, Object> beans = contextRefreshedEvent.getApplicationContext().getBeansWithAnnotation(
            Extension.class);
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }

        beans.entrySet().forEach(stringObjectEntry -> {
            T processor = (T)stringObjectEntry.getValue();
            Extension processorAnnotation = processor.getClass().getAnnotation(Extension.class);
            if (null != processorAnnotation) {
                for(String name : processorAnnotation.name()){
                    extensionMap.put(buildExtensionKey(processorAnnotation.type(), name, processorAnnotation.action()), processor);
                }
            }
        });
        log.info("ExtensionManagerSpringImpl onApplicationEvent {} ", extensionMap);
    }

    /**
     * 构建extensionKey
     *
     * @return key
     */
    private String buildExtensionKey(ExtensionTypeEnum type, String name, String action) {
        String buildExtensionKey = name.concat("_").concat(type.getCode());
        if(!StringUtils.isEmpty(action)){
            buildExtensionKey = action.concat("_").concat(buildExtensionKey);
        }
        log.info("buildExtensionKey--{}", buildExtensionKey);
        return buildExtensionKey;
    }

}