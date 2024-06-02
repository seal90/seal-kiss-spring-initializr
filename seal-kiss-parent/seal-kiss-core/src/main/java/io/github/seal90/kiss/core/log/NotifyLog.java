package io.github.seal90.kiss.core.log;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 通知log
 * 系统中由于降级（业务可正常返回，但仍旧是有损的）、特别关注事件等场景
 * 需要触发特别告警的场景使用
 */
@Slf4j
@Data
@Builder
public class NotifyLog {

    private static final Gson gson = new Gson();

    private String code;

    private String subCode;

    private String msg;

    public void log() {
        log.info(gson.toJson(this));
    }

    public static void log(NotifyLogScene notifyLogScene) {
        NotifyLog.builder().code(notifyLogScene.getCode())
                .subCode(notifyLogScene.getSubCode())
                .msg(notifyLogScene.getMsg()).build().log();
    }
}
