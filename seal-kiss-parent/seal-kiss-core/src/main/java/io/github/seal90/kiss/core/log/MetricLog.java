package io.github.seal90.kiss.core.log;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务指标统计
 * 用于统计业务指标
 */
@Slf4j
@Data
@Builder
public class MetricLog {

    private static final Gson gson = new Gson();

    private String code;

    private String subCode;

    private Long num;

    private String msg;

    public void log() {
        log.info(gson.toJson(this));
    }

    public static void log(MetricLogScene metricLogScene) {
        MetricLog.builder().code(metricLogScene.getCode()).subCode(metricLogScene.getSubCode())
                .num(metricLogScene.getNum()).msg(metricLogScene.getMsg()).build().log();
    }
}
