package io.github.seal90.kiss.core.exception;

import io.github.seal90.kiss.client.exception.ErrorReason;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 业务处理异常
 */
@Getter
public class ServiceException extends RuntimeException {

    /** 错误信息 */
    private ErrorReason errorReason;

    /** 特定错误信息 */
    @Setter
    private String specificErrMsg;

    /** 错误扩展信息会直接返回给调用者 */
    private Map<String, String> extInfo;

    public ServiceException(ErrorReason errorReason){
        super(errorReason.getErrMsg());
        this.errorReason = errorReason;

        this.extInfo = HashMap.newHashMap(8);
    }

    /**
     * 添加扩展信息
     *
     * @param key
     * @param value
     */
    public void putExtInfo(String key, String value) {
        this.extInfo.put(key, value);
    }

    /**
     * 设置扩展信息
     *
     * @param extInfos
     */
    public void putExtInfos(Map<String, String> extInfos) {
        if (null == extInfos || extInfos.size() <= 0) {
            return;
        }
        this.extInfo.putAll(extInfos);
    }
}
