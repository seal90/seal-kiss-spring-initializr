package io.github.seal90.kiss.core.utils;

import io.github.seal90.kiss.client.exception.SysErrorReason;
import io.github.seal90.kiss.core.exception.ServiceException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 基础校验
 */
public class AssertUtils {

    /**
     * 判断对象非空
     * @param obj
     * @param message
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * 判断对象是空
     * @param obj
     * @param message
     */
    public static void isNull(Object obj, String message) {
        if (obj != null) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * 判断字符串非“空”
     * @param str
     * @param message
     */
    public static void notEmpty(String str, String message) {
        if (null == str || str.isEmpty()) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * 判断字符串是有非空值
     * @param str
     * @param message
     */
    public static void hasText(String str, String message) {
        if (str != null && !str.isEmpty() && containsText(str)) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * 判断包含字符
     * @param str
     * @return
     */
    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断值是 true
     * @param expression
     * @param message
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * 判断值是 false
     * @param expression
     * @param message
     */
    public static void isFalse(boolean expression, String message) {
        if (expression) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * 数组非空
     * @param array
     * @param message
     */
    public static void notEmpty(Object[] array, String message) {
        if (ObjectUtils.isEmpty(array)) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * 数组不包含空对象
     * @param array
     * @param message
     */
    public static void noNullElements(Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
                    exception.setSpecificErrMsg(message);
                    throw exception;
                }
            }
        }
    }

    /**
     * 集合非空
     * @param collection
     * @param message
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * 集合不包含空对象
     * @param collection
     * @param message
     */
    public static void noNullElements(Collection<?> collection, String message) {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
                    exception.setSpecificErrMsg(message);
                    throw exception;
                }
            }
        }
    }

    /**
     * map 非空
     * @param map
     * @param message
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (CollectionUtils.isEmpty(map)) {
            ServiceException exception = new ServiceException(SysErrorReason.SYS_PARAMETER_ERROR);
            exception.setSpecificErrMsg(message);
            throw exception;
        }
    }

    /**
     * instanceOf 判断
     * @param type
     * @param obj
     * @param message
     */
    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            instanceCheckFailed(type, obj, message);
        }
    }

    private static void instanceCheckFailed(Class<?> type, Object obj, String msg) {
        String className = (obj != null ? obj.getClass().getName() : "null");
        String result = "";
        boolean defaultMessage = true;
        if (msg != null && !msg.isEmpty()) {
            if (endsWithSeparator(msg)) {
                result = msg + " ";
            }
            else {
                result = messageWithTypeName(msg, className);
                defaultMessage = false;
            }
        }
        if (defaultMessage) {
            result = result + ("Object of class [" + className + "] must be an instance of " + type);
        }
        throw new IllegalArgumentException(result);
    }

    private static boolean endsWithSeparator(String msg) {
        return (msg.endsWith(":") || msg.endsWith(";") || msg.endsWith(",") || msg.endsWith("."));
    }

    private static String messageWithTypeName(String msg, Object typeName) {
        return msg + (msg.endsWith(" ") ? "" : ": ") + typeName;
    }
}
