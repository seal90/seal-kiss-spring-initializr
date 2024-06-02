package io.github.seal90.kiss.core.log;

import java.lang.annotation.*;

/**
 * 监控日志
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MonitorLog {

    /**
     * 监控源
     * @return
     */
    String origin() default "";

    /**
     * 是否开启指标监控
     * 默认：开启
     * @return
     */
    boolean enableMetrics() default true;

    /**
     * 是否打印出入参数
     * 默认：关闭（通常此信息会较大，使用者根据实际情况处理）
     * @return
     */
    boolean enableArgs() default false;

    /**
     * 是否打印异常堆栈
     * 默认：关闭
     * @return
     */
    boolean enablePrintStackTrace() default false;
}
