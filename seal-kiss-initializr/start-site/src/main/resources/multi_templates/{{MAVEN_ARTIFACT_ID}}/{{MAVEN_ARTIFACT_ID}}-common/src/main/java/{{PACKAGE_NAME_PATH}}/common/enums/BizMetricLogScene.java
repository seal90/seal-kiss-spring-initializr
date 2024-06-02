package {{MAVEN_PACKAGE_NAME}}.common.enums;

import io.github.seal90.kiss.core.log.MetricLogScene;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizMetricLogScene implements MetricLogScene {

    TEST_METRIC_1("TEST_METRIC_1", "TEST_METRIC_SUB_1", "测试metric", 1L),

    ;

    String code;

    String subCode;

    String msg;

    Long num;
}
