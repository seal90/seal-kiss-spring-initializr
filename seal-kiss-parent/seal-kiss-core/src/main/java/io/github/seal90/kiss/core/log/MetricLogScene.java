package io.github.seal90.kiss.core.log;

/**
 * 监控指标项目
 */
public interface MetricLogScene {

    String getCode();

    String getSubCode();

    String getMsg();

    Long getNum();
}
