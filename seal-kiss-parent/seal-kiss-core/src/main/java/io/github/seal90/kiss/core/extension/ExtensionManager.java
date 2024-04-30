package io.github.seal90.kiss.core.extension;

/**
 * 扩展点管理器
 *
 */
public interface ExtensionManager {

    /**
     * 获取扩展点处理Bean
     *
     * @param type 类型
     * @param name 业务处理器名称，与扩展点Bean声明一致
     * @return 扩展点Bean
     */
    <T> T getExtension(ExtensionTypeEnum type, String name);

    /**
     * 获取扩展点处理Bean
     *
     * @param type 类型
     * @param name 业务处理器名称，与扩展点Bean声明一致
     * @param action 业务动作
     * @return 扩展点Bean
     */
    <T> T getExtension(ExtensionTypeEnum type, String name, String action);
}
