package io.github.seal90.kiss.core.config;

import io.github.seal90.kiss.base.exception.ErrorReason;
import io.github.seal90.kiss.base.exception.SysErrorReason;
import io.github.seal90.kiss.base.result.Result;
import io.github.seal90.kiss.core.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 程序异常处理
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class ExceptionAdviceConfig {

    private static final Map<Class, ErrorReason> EXCEPTION_ERROR_REASON;

    static {
        Map<Class, ErrorReason> classErrorReasonMap = new HashMap<>();
        classErrorReasonMap.put(BindException.class, SysErrorReason.SYS_PARAMETER_ERROR);
        classErrorReasonMap.put(MissingServletRequestParameterException.class, SysErrorReason.SYS_PARAMETER_ERROR);
        classErrorReasonMap.put(MethodArgumentTypeMismatchException.class, SysErrorReason.SYS_PARAMETER_ERROR);
        classErrorReasonMap.put(MethodArgumentNotValidException.class, SysErrorReason.SYS_PARAMETER_ERROR);
        classErrorReasonMap.put(IllegalArgumentException.class, SysErrorReason.SYS_PARAMETER_ERROR);
        classErrorReasonMap.put(HttpMessageNotReadableException.class, SysErrorReason.SYS_PARAMETER_ERROR);

        classErrorReasonMap.put(NoResourceFoundException.class, SysErrorReason.SYS_NOT_FOUND_ERROR);
        classErrorReasonMap.put(NoHandlerFoundException.class, SysErrorReason.SYS_NOT_FOUND_ERROR);

        EXCEPTION_ERROR_REASON = Collections.unmodifiableMap(classErrorReasonMap);
    }


    @ExceptionHandler(value = ServiceException.class)
    public Result serviceException(ServiceException exception){
        log.warn("业务异常", exception);
        if(null == exception.getSpecificErrMsg()) {
            return Result.error(exception.getErrorReason());
        }
        return Result.error(exception.getErrorReason(), exception.getSpecificErrMsg());
    }

    @ExceptionHandler(value = Exception.class)
    public Result exception(Exception exception){
        log.warn("未知异常", exception);
        ErrorReason reason = EXCEPTION_ERROR_REASON.get(exception.getClass());
        if(null != reason){
            return Result.error(reason, exception.getMessage());
        }
        return Result.error(SysErrorReason.ERROR);
    }
}