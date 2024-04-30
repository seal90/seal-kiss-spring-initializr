package io.github.seal90.kiss.core.extension;

/**
 * 标准处理器抽象类，用于处理器编排
 * 业务可以根据需要，继承该标准服务器，进行扩展
 * 流程包含准入判断及处理器执行
 *
 */
public abstract class AbstractStandardExtension<T, R> implements StandardExtension<T, R> {

    @Override
    public R process(T t, Object... params) {
        if (!isAllowed(t)) {
            return null;
        }
        return execute(t, params);
    }
}