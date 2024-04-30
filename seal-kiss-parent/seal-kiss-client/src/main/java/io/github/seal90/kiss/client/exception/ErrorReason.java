package io.github.seal90.kiss.client.exception;

/**
 * 异常原因
 */
public interface ErrorReason {

    /**
     * 获取错误码
     * @return
     */
    String getErrCode();

    /**
     * 获取错误信息
     * @return
     */
    String getErrMsg();
}
