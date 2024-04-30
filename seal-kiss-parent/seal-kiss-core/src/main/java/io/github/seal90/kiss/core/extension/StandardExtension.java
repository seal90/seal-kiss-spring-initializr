package io.github.seal90.kiss.core.extension;

/**
 * 标准扩展点
 */
public interface StandardExtension<T, R> {

    /**
     * 扩展点执行准入判断
     *
     * @param t 处理对象
     * @return 是否准入
     */
    boolean isAllowed(T t);

    /**
     * 执行扩展点处理逻辑
     *
     * @param t      处理对象
     * @param params 扩展参数
     * @return r
     */
    R execute(T t, Object... params);

    /**
     * 总流程编排
     *
     * @param t      处理对象
     * @param params 扩展参数
     * @return r
     */
    R process(T t, Object... params);
}