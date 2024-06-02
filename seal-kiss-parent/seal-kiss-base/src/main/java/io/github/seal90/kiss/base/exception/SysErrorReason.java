package io.github.seal90.kiss.base.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 程序基础异常
 */
@Getter
@AllArgsConstructor
public enum SysErrorReason implements ErrorReason {

    /** 响应成功 */
    OK("OK", "OK"),

    /** 响应失败，未捕获异常统一返回 */
    ERROR("ERROR", "Error"),

    /** 错误请求 */
    SYS_BAD_REQUEST_ERROR("SYS_BAD_REQUEST_ERROR", "Bad Request"),

    /** 不支持的请求 */
    SYS_NOT_FOUND_ERROR("SYS_NOT_FOUND_ERROR", "Not Found"),

    /** 重复请求 */
    SYS_REPEAT_REQUEST_ERROR("SYS_REPEAT_REQUEST_ERROR", "Repeat Request Error"),

    /** 参数异常 */
    SYS_PARAMETER_ERROR("SYS_PARAMETER_ERROR", "Parameter Error"),

    /** 认证信息异常 缺少/校验失败/过期 */
    SYS_UNAUTHORIZED_ERROR("SYS_UNAUTHORIZED_ERROR", "Unauthorized"),

    /** 权限不足 */
    SYS_FORBIDDEN_ERROR("SYS_FORBIDDEN_ERROR", "Forbidden"),
    ;
    private String code;
    private String msg;
}
