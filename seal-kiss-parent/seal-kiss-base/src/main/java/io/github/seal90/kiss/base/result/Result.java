package io.github.seal90.kiss.base.result;

import io.github.seal90.kiss.base.exception.ErrorReason;
import io.github.seal90.kiss.base.exception.SysErrorReason;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 返回信息
 * @param <T>
 */
@Getter
@Setter
public class Result<T> {

    /** 错误编码，用于判断返回结果后台业务是否处理成功 */
    private String code;

    @Setter
    /** 错误信息，用于返回错误原因的简单描述 */
    private String msg;

    /** 正常返回的数据 */
    @Setter
    private T data;

    /** 扩展信息 */
    private Map<String, String> extInfo;

    /**
     * 仅用做json反序列化使用
     * 请用 ok() error() 等方法构建返回值
     */
    @Deprecated(since = "0.0.0")
    public Result() {
        this.extInfo = HashMap.newHashMap(8);
    }

    public Result(ErrorReason errorReason) {
        Objects.requireNonNull(errorReason);
        Objects.requireNonNull(errorReason.getCode());
        Objects.requireNonNull(errorReason.getMsg());

        this.code = errorReason.getCode();
        this.msg = errorReason.getMsg();

        this.extInfo = HashMap.newHashMap(8);
    }

    public Result(ErrorReason errorReason, T data) {
        Objects.requireNonNull(errorReason);
        Objects.requireNonNull(errorReason.getCode());
        Objects.requireNonNull(errorReason.getMsg());

        this.code = errorReason.getCode();
        this.msg = errorReason.getMsg();

        this.data = data;

        this.extInfo = HashMap.newHashMap(8);
    }

    /**
     * 判断是否正确返回
     * @return
     */
    public Boolean isSuccess() {
        return SysErrorReason.OK.getCode().equals(this.code);
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
        result.setMsg(errMsg);
        return result;
    }

}
