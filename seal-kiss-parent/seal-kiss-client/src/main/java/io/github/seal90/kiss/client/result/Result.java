package io.github.seal90.kiss.client.result;

import io.github.seal90.kiss.client.exception.ErrorReason;
import io.github.seal90.kiss.client.exception.SysErrorReason;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class Result<T> {

    /** 错误编码，用于判断返回结果后台业务是否处理成功 */
    private String errCode;

    @Setter
    /** 错误信息，用于返回错误原因的简单描述 */
    private String errMsg;

    /** 正常返回的数据 */
    @Setter
    private T data;

    /** 扩展信息 */
    private Map<String, String> extInfo;

    public Result(ErrorReason errorReason) {
        Objects.requireNonNull(errorReason);
        Objects.requireNonNull(errorReason.getErrCode());
        Objects.requireNonNull(errorReason.getErrMsg());

        this.errCode = errorReason.getErrCode();
        this.errMsg = errorReason.getErrMsg();

        this.extInfo = HashMap.newHashMap(8);
    }

    public Result(ErrorReason errorReason, T data) {
        Objects.requireNonNull(errorReason);
        Objects.requireNonNull(errorReason.getErrCode());
        Objects.requireNonNull(errorReason.getErrMsg());

        this.errCode = errorReason.getErrCode();
        this.errMsg = errorReason.getErrMsg();

        this.data = data;

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

    /**
     * 仅返回处理成功
     * @return
     */
    public static Result<Void> ok(){
        return new Result<Void>(SysErrorReason.OK);
    }

    /**
     * 返回处理成功和数据
     * @param data
     * @return
     */
    public static <T> Result<T> ok(T data){
        return new Result<T>(SysErrorReason.OK, data);
    }

    /**
     * 仅返回处理失败
     * @param errorReason
     * @return
     */
    public static Result<Void> error(ErrorReason errorReason){
        return new Result<Void>(errorReason);
    }

    /**
     * 仅返回处理失败
     * 自定义错误描述
     * @param errorReason
     * @return
     */
    public static Result<Void> error(ErrorReason errorReason, String errMsg){
        Result<Void> result = new Result<>(errorReason);
        result.setErrMsg(errMsg);
        return result;
    }

}
