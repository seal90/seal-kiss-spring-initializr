package com.github.seal90.kiss.multi.common.enums;

import io.github.seal90.kiss.core.log.NotifyLogScene;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BizNotifyLogScene implements NotifyLogScene {

    TEST_NOTIFY_1("TEST_NOTIFY_1", "TEST_NOTIFY_1", "测试"),

    ;

    String code;

    String subCode;

    String msg;
}
