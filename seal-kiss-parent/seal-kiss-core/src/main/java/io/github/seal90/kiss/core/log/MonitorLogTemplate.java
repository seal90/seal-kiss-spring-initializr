package io.github.seal90.kiss.core.log;

import java.util.function.Function;

/**
 * 监控打印模板
 */
public interface MonitorLogTemplate {

    /**
     * 打印指标
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     */
    <T,R> R logMetrics(String origin, String identifier, Function<T,R> fun, T args);

    /**
     * 打印出入参数
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     */
    <T,R> R logArgs(String origin, String identifier, Function<T,R> fun, T args);

    /**
     * 打印指标和参数
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     */
    <T,R> R logMetricsArgs(String origin, String identifier, Function<T,R> fun, T args);

    /**
     * 自定义log
     * @param logMetric
     * @param logArgs
     * @param enablePrintStackTrace
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     */
    <T, R> R log(Boolean logMetric, Boolean logArgs, Boolean enablePrintStackTrace,
                 String origin, String identifier, Function<T, R> fun, T args);

    /**
     * 带异常处理的自定义log
     * @param logMetric
     * @param logArgs
     * @param enablePrintStackTrace
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     * @throws Throwable
     */
    <T, R> R logWithException(Boolean logMetric, Boolean logArgs, Boolean enablePrintStackTrace,
                              String origin, String identifier, MonitorFunction<T, R> fun, T args) throws Throwable;
}
