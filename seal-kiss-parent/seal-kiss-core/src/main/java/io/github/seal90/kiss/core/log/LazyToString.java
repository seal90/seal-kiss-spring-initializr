package io.github.seal90.kiss.core.log;

/**
 * 延迟序列化对象
 * 在打印日志时，非对应级别的日志可以不用序列化
 */
public interface LazyToString {

    LazyToString obj(Object object);
}
