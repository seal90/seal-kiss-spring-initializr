package io.github.seal90.kiss.core.log;

import lombok.Getter;

@Getter
public enum SysNotifyLogScene implements NotifyLogScene {

    SYS_GLOBAL_EXCEPTION_COMMIT("SYS_GLOBAL_EXCEPTION", "COMMIT", "全局异常处理捕获已经响应后产生的异常"),
    SYS_GLOBAL_EXCEPTION_UNEXPECTED("SYS_GLOBAL_EXCEPTION", "UNEXPECTED", "全局异常处理捕获未处理的异常"),

    ;
    
    private String code;

    private String subCode;

    private String msg;

    SysNotifyLogScene(String code, String subCode, String msg) {
        this.code = code;
        this.subCode = subCode;
        this.msg = msg;
    }
}
