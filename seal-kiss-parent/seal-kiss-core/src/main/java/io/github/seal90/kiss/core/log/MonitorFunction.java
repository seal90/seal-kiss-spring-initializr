package io.github.seal90.kiss.core.log;

@FunctionalInterface
public interface MonitorFunction<T, R> {

    /**
     * 含异常的方法执行
     * @param t
     * @return
     * @throws Throwable
     */
    R apply(T t) throws Throwable;
}