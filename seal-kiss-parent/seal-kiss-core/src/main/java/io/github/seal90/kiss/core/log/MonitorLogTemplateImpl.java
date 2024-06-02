package io.github.seal90.kiss.core.log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import io.github.seal90.kiss.base.result.Result;
import io.github.seal90.kiss.core.exception.ServiceException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

@Slf4j
public class MonitorLogTemplateImpl implements MonitorLogTemplate {

    private static final Logger DIGIT_LOG = LoggerFactory.getLogger("MONITOR_DIGIT_LOG_LOGGER");

    private final Gson gson;

    private final Boolean metricJsonFlag;

    public MonitorLogTemplateImpl(Gson gson, Boolean metricJsonFlag) {
        this.gson = gson;
        this.metricJsonFlag = metricJsonFlag;
    }

    /**
     * 打印指标
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     */
    @Override
    public <T, R> R logMetrics(String origin, String identifier, Function<T, R> fun, T args) {
        R resp = this.log(true, false, false, origin, identifier, fun, args);
        return resp;
    }

    /**
     * 打印出入参数
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     */
    @Override
    public <T, R> R logArgs(String origin, String identifier, Function<T, R> fun, T args) {
        R resp = this.log(false, true, false, origin, identifier, fun, args);
        return resp;
    }

    /**
     * 打印指标和参数
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     */
    @Override
    public <T, R> R logMetricsArgs(String origin, String identifier, Function<T, R> fun, T args) {
        R resp = this.log(true, true, false, origin, identifier, fun, args);
        return resp;
    }

    /**
     * 打印日志
     * @param logMetric
     * @param logArgs
     * @param enablePrintStackTrace
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     */
    public <T, R> R log(Boolean logMetric, Boolean logArgs, Boolean enablePrintStackTrace, String origin, String identifier, Function<T, R> fun, T args) {

        long startTime = System.currentTimeMillis();

        String bizSuccessFlag = "Y";
        String noThrowFlag = "Y";
        long realTime = 0;

        if(Boolean.TRUE.equals(logArgs)){
            // [Args] [identifier] args
            log.info("[Args] [{}] {}", identifier, gson.toJson(args));
        }
        try {
            R resp = fun.apply(args);
            if (resp instanceof Result<?> result && (!result.isSuccess())) {
                bizSuccessFlag = "N";
            }
            realTime = System.currentTimeMillis() - startTime;
            if (Boolean.TRUE.equals(logArgs)) {
                // [Result] [identifier] [realTime] return value
                log.info("[Result] [{}] [{}] {}", identifier, realTime, gson.toJson(resp));
            }
            return resp;
        } catch (ServiceException e) {
            realTime = System.currentTimeMillis() - startTime;
            if(Boolean.TRUE.equals(logArgs)) {
                // [Throw] [identifier] [realTime] exception_name exception_message
                log.info("[Throw] [{}] [{}] {} {}", identifier, realTime, e.getClass().getSimpleName(), e.getMessage());
            }
            if(Boolean.TRUE.equals(enablePrintStackTrace)) {
                log.warn("请求业务异常", e);
            }
            bizSuccessFlag = "N";
            noThrowFlag = "Y";

            throw e;
        } catch (Throwable t) {
            realTime = System.currentTimeMillis() - startTime;
            if(Boolean.TRUE.equals(logArgs)) {
                // [Throw] [identifier] [realTime] exception_name exception_message
                log.info("[Throw] [{}] [{}] {} {}", identifier, realTime, t.getClass().getSimpleName(), t.getMessage());
            }
            if(Boolean.TRUE.equals(enablePrintStackTrace)) {
                log.warn("请求异常", t);
            }
            bizSuccessFlag = "N";
            noThrowFlag = "N";

            throw t;
        } finally {
            if(Boolean.TRUE.equals(logMetric)) {
                if(metricJsonFlag) {
                    MetricLog metricLog = MetricLog.builder().origin(origin).identifier(identifier).realTime(realTime).bizSuccessFlag(bizSuccessFlag).noThrowFlag(noThrowFlag).build();
                    DIGIT_LOG.info(gson.toJson(metricLog));
                } else {
                    // METRICS,{origin},{identifier},{realTime},{bizSuccessFlag},{noThrowFlag}
                    DIGIT_LOG.info("METRICS,{},{},{},{},{}", origin, identifier, realTime, bizSuccessFlag, noThrowFlag);
                }
            }
        }
    }

    /**
     * 打印带有检查异常的处理
     * 打印逻辑同上面一致
     * @param logMetric
     * @param logArgs
     * @param enablePrintStackTrace
     * @param origin
     * @param identifier
     * @param fun
     * @param args
     * @return
     * @param <T>
     * @param <R>
     * @throws Throwable
     */
    public <T, R> R logWithException(Boolean logMetric, Boolean logArgs, Boolean enablePrintStackTrace, String origin, String identifier, MonitorFunction<T, R> fun, T args) throws Throwable {

        long startTime = System.currentTimeMillis();

        String bizSuccessFlag = "Y";
        String noThrowFlag = "Y";
        long realTime = 0;

        if(Boolean.TRUE.equals(logArgs)){
            // [Args] [identifier] args
            log.info("[Args] [{}] {}", identifier, gson.toJson(args));
        }
        try {
            R resp = fun.apply(args);
            if (resp instanceof Result<?> result && (!result.isSuccess())) {
                bizSuccessFlag = "N";
            }
            realTime = System.currentTimeMillis() - startTime;
            if(Boolean.TRUE.equals(logArgs)) {
                // [Result] [identifier] [realTime] return value
                log.info("[Result] [{}] [{}] {}", identifier, realTime, gson.toJson(resp));
            }
            return resp;
        } catch (ServiceException e) {
            realTime = System.currentTimeMillis() - startTime;
            if(Boolean.TRUE.equals(logArgs)) {
                // [Throw] [identifier] [realTime] exception_name exception_message
                log.info("[Throw] [{}] [{}] {} {}", identifier, realTime, e.getClass().getSimpleName(), e.getMessage());
            }
            if(Boolean.TRUE.equals(enablePrintStackTrace)) {
                log.warn("请求业务异常", e);
            }
            bizSuccessFlag = "N";
            noThrowFlag = "Y";

            throw e;
        } catch (Throwable t) {
            realTime = System.currentTimeMillis() - startTime;
            if(Boolean.TRUE.equals(logArgs)) {
                // [Throw] [identifier] [realTime] exception_name exception_message
                log.info("[Throw] [{}] [{}] {} {}", identifier, realTime, t.getClass().getSimpleName(), t.getMessage());
            }
            if(Boolean.TRUE.equals(enablePrintStackTrace)) {
                log.warn("处理异常", t);
            }
            bizSuccessFlag = "N";
            noThrowFlag = "N";

            throw t;
        } finally {
            if(Boolean.TRUE.equals(logMetric)) {
                if (metricJsonFlag) {
                    MetricLog metricLog = MetricLog.builder().origin(origin).identifier(identifier).realTime(realTime).bizSuccessFlag(bizSuccessFlag).noThrowFlag(noThrowFlag).build();
                    DIGIT_LOG.info(gson.toJson(metricLog));
                } else {
                    // METRICS,{origin},{identifier},{realTime},{bizSuccessFlag},{noThrowFlag}
                    DIGIT_LOG.info("METRICS,{},{},{},{},{}", origin, identifier, realTime, bizSuccessFlag, noThrowFlag);
                }
            }
        }
    }

    @Data
    @Builder
    private static final class MetricLog {
        @SerializedName("o")
        private String origin;
        @SerializedName("id")
        private String identifier;
        @SerializedName("rt")
        private Long realTime;
        @SerializedName("bsf")
        private String bizSuccessFlag;
        @SerializedName("ntf")
        private String noThrowFlag;
    }
}
